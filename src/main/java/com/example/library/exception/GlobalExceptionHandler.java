package com.example.library.exception;

import com.example.library.dto.response.ApiResponse;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;

/**
 * Global exception handler for REST controllers.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	/**
	 * Handles CustomException thrown by the application.
	 *
	 * @param ex      CustomException instance
	 * @param request WebRequest instance
	 * @return ResponseEntity with error details
	 */
	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ApiResponse<Object>> handleCustomException(
			CustomException ex, WebRequest request) {
		log.error("CustomException: {}", ex.getMessage());
		ApiResponse<Object> response = ApiResponse.builder()
				.status(ex.getStatus().value())
				.message(ex.getMessage())
				.timestamp(LocalDateTime.now())
				.build();
		return new ResponseEntity<>(response, ex.getStatus());
	}

	/**
	 * Handles all other exceptions not handled elsewhere.
	 *
	 * @param ex      Exception instance
	 * @param request WebRequest instance
	 * @return ResponseEntity with error details
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Object>> handleAllExceptions(
			Exception ex, WebRequest request) {
		log.error("Exception: ", ex);
		ApiResponse<Object> response = ApiResponse.builder()
				.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
				.message("An unexpected error occurred")
				.timestamp(LocalDateTime.now())
				.build();
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
