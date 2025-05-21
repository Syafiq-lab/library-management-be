package com.mb.transactionbackend.dto;

import jakarta.validation.constraints.NotBlank;

public record LoanRequest(
        @NotBlank String borrowerId,
        @NotBlank String bookId
) {}