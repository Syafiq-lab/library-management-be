package com.example.qrservice.web.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class QrDecodeResponse {
    String payload;
    String type;
}
