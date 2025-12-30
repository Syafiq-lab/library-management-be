package com.example.qrservice.web;

import com.example.qrservice.service.QrService;
import com.example.qrservice.dto.QrDecodeRequest;
import com.example.qrservice.dto.QrDecodeResponse;
import com.example.qrservice.dto.QrGenerateRequest;
import com.example.qrservice.dto.QrGenerateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/qr")
@RequiredArgsConstructor
@Tag(name = "QR Service", description = "Generate and decode QR codes")
public class QrController {

    private final QrService qrService;

    @PostMapping(
            path = "/generate",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Generate a QR code for the given payload")
    public ResponseEntity<QrGenerateResponse> generate(
            @Validated @RequestBody QrGenerateRequest request) {
        
        String payload = request.getPayload();
        log.debug("QR generate requested: type={}, size={}, payloadLen={}, payloadHash={}",
                request.getType(),
                request.getSize(),
                payload == null ? 0 : payload.length(),
                QrService.safeHash(payload));
return ResponseEntity.ok(qrService.generate(request));
    }

    @PostMapping(
            path = "/decode",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Decode a QR code image (Base64 PNG)")
    public ResponseEntity<QrDecodeResponse> decode(
            @Validated @RequestBody QrDecodeRequest request) {
        
        String img = request.getImageBase64();
        log.debug("QR decode requested: imageBase64Len={}", img == null ? 0 : img.length());
return ResponseEntity.ok(qrService.decode(request));
    }
}
