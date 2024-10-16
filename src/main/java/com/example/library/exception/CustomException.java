package com.example.library.exception;

import org.springframework.http.HttpStatus;
import lombok.Getter;

/**
 * Custom exception class for application-specific exceptions.
 */
@Getter
public class CustomException extends RuntimeException {
	private final HttpStatus status;
	private final String message;

	/**
	 * Constructs a new CustomException.
	 *
	 * @param message Error message
	 * @param status  HTTP status code
	 */
	public CustomException(String message, HttpStatus status) {
		super(message);
		this.message = message;
		this.status = status;
	}
}
