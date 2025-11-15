package com.example.inventoryservice.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Value;

import java.math.BigDecimal;

@Value
public class InventoryItemCreateRequest {

    @NotBlank
    String name;

    @Min(0)
    int quantity;

    @NotNull
    @Positive
    BigDecimal unitPrice;

    @NotNull
    Long ownerUserId;
}
