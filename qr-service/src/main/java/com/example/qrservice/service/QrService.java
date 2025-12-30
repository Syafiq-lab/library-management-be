package com.example.qrservice.service;

import com.example.qrservice.domain.QrCodeRecord;
import com.example.qrservice.event.QrDecodedEvent;
import com.example.qrservice.event.QrGeneratedEvent;
import com.example.qrservice.repository.QrCodeRecordRepository;
import com.example.qrservice.dto.QrDecodeRequest;
import com.example.qrservice.dto.QrDecodeResponse;
import com.example.qrservice.dto.QrGenerateRequest;
import com.example.qrservice.dto.QrGenerateResponse;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class QrService {

    private final QrCodeRecordRepository repository;
    private final StreamBridge streamBridge;

    public QrGenerateResponse generate(QrGenerateRequest request) {
        
        String payload = request.getPayload();
        log.debug("Generating QR: type={}, requestedSize={}, payloadLen={}, payloadHash={}",
                request.getType(),
                request.getSize(),
                payload == null ? 0 : payload.length(),
                safeHash(payload));
int size = request.getSize() != null && request.getSize() > 0
                ? request.getSize()
                : 256;

        // 1. Generate PNG bytes and Base64
        String imageBase64 = generateQrBase64(request.getPayload(), size);

        // 2. Persist a record
        QrCodeRecord record = QrCodeRecord.builder()
                .payload(request.getPayload())
                .type(request.getType())
                .createdAt(Instant.now())
                .build();

        record = repository.save(record);

        
        log.debug("QR record persisted: id={}, type={}, createdAt={}", record.getId(), record.getType(), record.getCreatedAt());
// 3. Publish MQ event
        QrGeneratedEvent event = QrGeneratedEvent.builder()
                .id(record.getId())
                .payload(record.getPayload())
                .type(record.getType())
                .createdAt(record.getCreatedAt())
                .build();

        boolean sent = streamBridge.send("qrGenerated-out-0", event);
        if (sent) {
            log.debug("QrGeneratedEvent published: destination=qrGenerated-out-0 id={}", record.getId());
        } else {
            log.warn("QrGeneratedEvent publish returned false: destination=qrGenerated-out-0 id={}", record.getId());
        }
// 4. Build response
        return QrGenerateResponse.builder()
                .id(record.getId())
                .payload(record.getPayload())
                .type(record.getType())
                .imageBase64(imageBase64)
                .build();
    }

    public QrDecodeResponse decode(QrDecodeRequest request) {
        
        String img = request.getImageBase64();
        log.debug("Decoding QR: imageBase64Len={}", img == null ? 0 : img.length());
String payload = decodeQrBase64(request.getImageBase64());

        
        log.debug("QR decoded: payloadLen={}, payloadHash={}", payload == null ? 0 : payload.length(), safeHash(payload));
QrDecodeResponse response = QrDecodeResponse.builder()
                .payload(payload)
                .type("UNKNOWN")
                .build();

        // Publish MQ event for decoding
        QrDecodedEvent event = QrDecodedEvent.builder()
                .recordId(null)
                .payload(payload)
                .type(response.getType())
                .decodedAt(Instant.now())
                .build();

        boolean sent = streamBridge.send("qrDecoded-out-0", event);
        if (sent) {
            log.debug("QrDecodedEvent published: destination=qrDecoded-out-0 payloadHash={}", safeHash(payload));
        } else {
            log.warn("QrDecodedEvent publish returned false: destination=qrDecoded-out-0 payloadHash={}", safeHash(payload));
        }
return response;
    }
    /**
     * Safe short hash for logging (avoids logging raw payloads / tokens).
     */
    public static String safeHash(String value) {
        if (value == null || value.isBlank()) return "NA";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 4 && i < hash.length; i++) {
                sb.append(String.format("%02x", hash[i]));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return "NA";
        }
    }


    private String generateQrBase64(String payload, int size) {
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1);

            BitMatrix matrix = new MultiFormatWriter()
                    .encode(payload, BarcodeFormat.QR_CODE, size, size, hints);

            BufferedImage image = MatrixToImageWriter.toBufferedImage(matrix);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", baos);

            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            log.warn("Failed to generate QR code: size={}, payloadHash={}", size, safeHash(payload), e);
            throw new IllegalStateException("Failed to generate QR code", e);
        }
    }

    private String decodeQrBase64(String imageBase64) {
        try {
            byte[] bytes = Base64.getDecoder().decode(imageBase64);
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            BufferedImage image = ImageIO.read(bais);

            BinaryBitmap bitmap = new BinaryBitmap(
                    new HybridBinarizer(new BufferedImageLuminanceSource(image))
            );

            Result result = new MultiFormatReader().decode(bitmap);
            return result.getText();
        } catch (Exception e) {
            log.warn("Failed to decode QR code: imageBase64Len={}", imageBase64 == null ? 0 : imageBase64.length(), e);
            throw new IllegalStateException("Failed to decode QR code", e);
        }
    }
}
