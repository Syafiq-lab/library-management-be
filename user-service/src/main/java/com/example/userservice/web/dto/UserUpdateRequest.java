package com.example.userservice.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Value;

@Value
public class UserUpdateRequest {

    @NotBlank
    @Size(min = 3, max = 120)
    String fullName;

    @NotBlank
    @Email
    String email;

    boolean active;
}
