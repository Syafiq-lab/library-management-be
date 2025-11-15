package com.example.userservice.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Value;

@Value
public class UserCreateRequest {

    @NotBlank
    @Size(min = 3, max = 50)
    String username;

    @NotBlank
    @Email
    String email;

    @NotBlank
    @Size(min = 3, max = 120)
    String fullName;
}
