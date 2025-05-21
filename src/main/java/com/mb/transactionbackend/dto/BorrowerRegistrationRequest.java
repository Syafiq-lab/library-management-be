package com.mb.transactionbackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record BorrowerRegistrationRequest(
        @NotBlank String borrowerId,
        @NotBlank String name,
        @Email String email
) {}