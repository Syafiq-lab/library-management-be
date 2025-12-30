package com.example.qrservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class QrDecodeRequest {

    @NotBlank
    String imageBase64;
}
