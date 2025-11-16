package com.example.qrservice.service;

import com.example.qrservice.domain.QrCodeRecord;
import com.example.qrservice.event.QrDecodedEvent;
import com.example.qrservice.event.QrGeneratedEvent;
import com.example.qrservice.repository.QrCodeRecordRepository;
import com.example.qrservice.web.dto.QrDecodeRequest;
import com.example.qrservice.web.dto.QrDecodeResponse;
import com.example.qrservice.web.dto.QrGenerateRequest;
import com.example.qrservice.web.dto.QrGenerateResponse;
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

@Service
@RequiredArgsConstructor
@Transactional
public class QrService {

    private final QrCodeRecordRepository repository;
    private final StreamBridge streamBridge;

    public QrGenerateResponse generate(QrGenerateRequest request) {
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

        // 3. Publish MQ event
        QrGeneratedEvent event = QrGeneratedEvent.builder()
                .id(record.getId())
                .payload(record.getPayload())
                .type(record.getType())
                .createdAt(record.getCreatedAt())
                .build();

        streamBridge.send("qrGenerated-out-0", event);

        // 4. Build response
        return QrGenerateResponse.builder()
                .id(record.getId())
                .payload(record.getPayload())
                .type(record.getType())
                .imageBase64(imageBase64)
                .build();
    }

    public QrDecodeResponse decode(QrDecodeRequest request) {
        String payload = decodeQrBase64(request.getImageBase64());

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

        streamBridge.send("qrDecoded-out-0", event);

        return response;
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
            throw new IllegalStateException("Failed to decode QR code", e);
        }
    }
}
