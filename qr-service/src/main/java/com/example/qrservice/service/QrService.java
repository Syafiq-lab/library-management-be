package com.example.qrservice.service;

import com.example.qrservice.domain.QrCodeRecord;
import com.example.qrservice.repository.QrCodeRecordRepository;
import com.example.qrservice.web.dto.QrDecodeRequest;
import com.example.qrservice.web.dto.QrDecodeResponse;
import com.example.qrservice.web.dto.QrGenerateRequest;
import com.example.qrservice.web.dto.QrGenerateResponse;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.util.Base64;
import java.util.EnumMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QrService {

    private final QrCodeRecordRepository repository;

    @Transactional
    public QrGenerateResponse generate(QrGenerateRequest request) {
        String imageBase64 = generateQrBase64(request.getPayload(), request.getSize());
        QrCodeRecord record = QrCodeRecord.builder()
                .payload(request.getPayload())
                .type(request.getType())
                .createdAt(Instant.now())
                .build();
        QrCodeRecord saved = repository.save(record);
        return QrGenerateResponse.builder()
                .id(saved.getId())
                .payload(saved.getPayload())
                .type(saved.getType())
                .imageBase64(imageBase64)
                .build();
    }

    @Transactional(readOnly = true)
    public QrDecodeResponse decode(QrDecodeRequest request) {
        String payload = decodeQrBase64(request.getImageBase64());
        return QrDecodeResponse.builder()
                .payload(payload)
                .type("UNKNOWN")
                .build();
    }

    private String generateQrBase64(String payload, int size) {
        try {
            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1);
            BitMatrix matrix = new MultiFormatWriter().encode(payload, BarcodeFormat.QR_CODE, size, size, hints);
            BufferedImage image = MatrixToImageWriter.toBufferedImage(matrix);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
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
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            Result result = new MultiFormatReader().decode(bitmap);
            return result.getText();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to decode QR code", e);
        }
    }
}
