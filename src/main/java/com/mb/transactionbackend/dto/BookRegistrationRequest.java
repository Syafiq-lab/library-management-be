package com.mb.transactionbackend.dto;

import jakarta.validation.constraints.NotBlank;

public record BookRegistrationRequest(
        @NotBlank String bookId,
        @NotBlank String isbn,
        @NotBlank String title,
        @NotBlank String author
) {}