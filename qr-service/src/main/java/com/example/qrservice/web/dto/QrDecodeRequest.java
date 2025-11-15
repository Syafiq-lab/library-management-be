package com.example.qrservice.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class QrDecodeRequest {

    @NotBlank
    String imageBase64;
}
