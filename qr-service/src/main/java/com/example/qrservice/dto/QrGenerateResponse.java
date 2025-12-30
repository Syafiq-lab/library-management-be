package com.example.qrservice.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class QrGenerateResponse {
    Long id;
    String payload;
    String type;
    String imageBase64;
}
