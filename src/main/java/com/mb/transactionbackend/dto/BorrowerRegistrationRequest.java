package com.mb.transactionbackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BorrowerRegistrationRequest(
        @NotBlank String borrowerId,
        @NotBlank String name,
		@Email @NotNull String email
) {}