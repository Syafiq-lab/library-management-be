package com.example.library.dto.response;

import lombok.Data;

/**
 * Data Transfer Object for JWT authentication responses.
 */
@Data
public class JwtResponse {

	private String accessToken;
	private String tokenType = "Bearer";

	public JwtResponse(String accessToken) {
		this.accessToken = accessToken;
	}
}
