package com.example.qrservice.web;

import com.example.qrservice.service.QrService;
import com.example.qrservice.web.dto.QrDecodeRequest;
import com.example.qrservice.web.dto.QrDecodeResponse;
import com.example.qrservice.web.dto.QrGenerateRequest;
import com.example.qrservice.web.dto.QrGenerateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/qr")
@RequiredArgsConstructor
@Tag(name = "QR", description = "QR generator and reader APIs")
public class QrController {

    private final QrService service;

    @PostMapping("/generate")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Generate QR code from payload")
    public QrGenerateResponse generate(@Valid @RequestBody QrGenerateRequest request) {
        return service.generate(request);
    }

    @PostMapping("/decode")
    @Operation(summary = "Decode QR code from base64 image")
    public QrDecodeResponse decode(@Valid @RequestBody QrDecodeRequest request) {
        return service.decode(request);
    }
}
