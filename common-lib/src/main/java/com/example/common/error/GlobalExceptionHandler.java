package com.example.common.error;

import com.example.common.api.ApiResponse;
import com.example.common.exception.BusinessException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
			MethodArgumentNotValidException ex,
			HttpHeaders headers,
			HttpStatusCode status,
			WebRequest request) {

		List<String> errors = ex.getBindingResult()
				.getFieldErrors().stream()
				.map(fe -> fe.getField() + " " + fe.getDefaultMessage())
				.collect(Collectors.toList());

		ApiResponse<Void> body = ApiResponse.error(
				HttpStatus.BAD_REQUEST,
				"Validation failed",
				errors
		);
		return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException ex) {
		List<String> errors = ex.getConstraintViolations().stream()
				.map(v -> v.getPropertyPath() + " " + v.getMessage())
				.toList();

		ApiResponse<Void> body = ApiResponse.error(
				HttpStatus.BAD_REQUEST,
				"Validation failed",
				errors
		);
		return ResponseEntity.badRequest().body(body);
	}

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException ex) {
		List<String> errors = List.of(ex.getMessage());

		ApiResponse<Void> body = ApiResponse.error(
				ex.getHttpStatus(),
				"Business error",
				errors
		);
		return new ResponseEntity<>(body, ex.getHttpStatus());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Void>> handleOther(Exception ex) {
		ApiResponse<Void> body = ApiResponse.error(
				HttpStatus.INTERNAL_SERVER_ERROR,
				"Internal server error",
				List.of("Unexpected error: " + ex.getClass().getSimpleName())
		);
		return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
