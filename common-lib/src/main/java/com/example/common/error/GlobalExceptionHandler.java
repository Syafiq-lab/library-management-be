package com.example.common.error;

import com.example.common.api.ApiResponse;
import com.example.common.exception.BusinessException;
import com.example.common.exception.NotFoundException;
import com.example.common.exception.UserNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .collect(Collectors.toList());

        log.warn("Validation failed | {} | errors={}", request.getDescription(false), errors);

        ApiResponse<Void> body = ApiResponse.error(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                errors
        );
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.toList());

        log.warn("Constraint violation | {} | errors={}", request.getDescription(false), errors);

        ApiResponse<Void> body = ApiResponse.error(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                errors
        );
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({NotFoundException.class, UserNotFoundException.class})
    public ResponseEntity<ApiResponse<Void>> handleNotFound(RuntimeException ex, WebRequest request) {
        log.warn("Not found | {} | type={} | msg={}",
                request.getDescription(false),
                ex.getClass().getSimpleName(),
                ex.getMessage());

        ApiResponse<Void> body = ApiResponse.error(
                HttpStatus.NOT_FOUND,
                "Not found",
                List.of(ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName())
        );
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException ex, WebRequest request) {
        // Keep it generic to avoid calling methods that may not exist on your BusinessException implementation.
        log.warn("Business exception | {} | type={} | msg={}",
                request.getDescription(false),
                ex.getClass().getSimpleName(),
                ex.getMessage());

        ApiResponse<Void> body = ApiResponse.error(
                HttpStatus.BAD_REQUEST,
                ex.getMessage() != null ? ex.getMessage() : "Business rule violated",
                List.of(ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName())
        );
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnhandled(Exception ex, WebRequest request) {
        log.error("Unhandled exception | {} | type={} | msg={}",
                request.getDescription(false),
                ex.getClass().getName(),
                ex.getMessage(),
                ex);

        ApiResponse<Void> body = ApiResponse.error(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error",
                List.of("Unexpected error: " + ex.getClass().getSimpleName())
        );
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String formatFieldError(FieldError e) {
        return e.getField() + ": " + (e.getDefaultMessage() == null ? "invalid" : e.getDefaultMessage());
    }
}
