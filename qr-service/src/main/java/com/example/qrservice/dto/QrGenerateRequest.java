package com.example.qrservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Value;

@Value
public class QrGenerateRequest {

    @NotBlank
    String payload;

    @NotBlank
    String type;

    @NotNull
    @Positive
    Integer size;
}
