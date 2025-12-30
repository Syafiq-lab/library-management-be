package com.example.inventoryservice.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;

@Value
@Builder
public class InventoryItemResponse {
    Long id;
    String name;
    int quantity;
    BigDecimal unitPrice;
    Long ownerUserId;
    Instant createdAt;
    Instant updatedAt;
}
