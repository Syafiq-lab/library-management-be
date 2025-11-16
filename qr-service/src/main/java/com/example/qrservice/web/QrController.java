package com.example.qrservice.web;

import com.example.qrservice.service.QrService;
import com.example.qrservice.web.dto.QrDecodeRequest;
import com.example.qrservice.web.dto.QrDecodeResponse;
import com.example.qrservice.web.dto.QrGenerateRequest;
import com.example.qrservice.web.dto.QrGenerateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
        return ResponseEntity.ok(qrService.decode(request));
    }
}
