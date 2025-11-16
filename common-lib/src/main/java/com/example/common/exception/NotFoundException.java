package com.example.common.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends BusinessException {

	public NotFoundException(String message, String errorCode) {
		super(message, errorCode, HttpStatus.NOT_FOUND);
	}
}
