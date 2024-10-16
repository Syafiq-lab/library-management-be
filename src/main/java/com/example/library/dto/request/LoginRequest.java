package com.example.library.dto.request;

import lombok.Data;
import javax.validation.constraints.*;

/**
 * Data Transfer Object for login requests.
 */
@Data
public class LoginRequest {

	@NotBlank(message = "Username is mandatory")
	private String username;

	@NotBlank(message = "Password is mandatory")
	private String password;
}
