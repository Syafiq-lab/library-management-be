package com.example.library.dto.request;

import lombok.Data;
import javax.validation.constraints.*;

/**
 * Data Transfer Object for borrower registration requests.
 */
@Data
public class BorrowerRequest {

	@NotBlank(message = "Borrower ID is mandatory")
	private String borrowerId;

	@NotBlank(message = "Name is mandatory")
	private String name;

	@Email(message = "Email should be valid")
	@NotBlank(message = "Email is mandatory")
	private String email;

	@NotBlank(message = "Username is mandatory")
	private String username;

	@NotBlank(message = "Password is mandatory")
	private String password;
}
