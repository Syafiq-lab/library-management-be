package com.example.userservice.web.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserUpdateRequest {

    private String username;

    @Email
    private String email;

    private String fullName;

    private Boolean active;
}
