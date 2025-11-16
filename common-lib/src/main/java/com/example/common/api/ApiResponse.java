package com.example.common.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

	private final ApiStatus status;    // SUCCESS / ERROR
	private final int code;            // HTTP status code (200, 400, 500, ...)
	private final String message;
	private final T payload;
	private final List<String> errors;
	private final Instant timestamp;
	private final String traceId;

	// main private constructor
	private ApiResponse(ApiStatus status,
						int code,
						String message,
						T payload,
						List<String> errors,
						String traceId) {

		this.status = status;
		this.code = code;
		this.message = message;
		this.payload = payload;
		this.errors = errors;
		this.traceId = traceId;
		this.timestamp = Instant.now();
	}

	/* ========== SUCCESS HELPERS ========== */

	// default 200 OK
	public static <T> ApiResponse<T> success(String message, T payload) {
		return success(HttpStatus.OK, message, payload);
	}

	// explicit status, e.g. CREATED
	public static <T> ApiResponse<T> success(HttpStatus httpStatus, String message, T payload) {
		return new ApiResponse<>(
				ApiStatus.SUCCESS,
				httpStatus.value(),
				message,
				payload,
				null,
				null
		);
	}

	public static <T> ApiResponse<T> success(HttpStatus httpStatus, String message, T payload, String traceId) {
		return new ApiResponse<>(
				ApiStatus.SUCCESS,
				httpStatus.value(),
				message,
				payload,
				null,
				traceId
		);
	}

	/* ========== ERROR HELPERS ========== */

	// default 500
	public static <T> ApiResponse<T> error(String message, List<String> errors) {
		return error(HttpStatus.INTERNAL_SERVER_ERROR, message, errors, null);
	}

	public static <T> ApiResponse<T> error(HttpStatus httpStatus,
										   String message,
										   List<String> errors) {
		return error(httpStatus, message, errors, null);
	}

	public static <T> ApiResponse<T> error(HttpStatus httpStatus,
										   String message,
										   List<String> errors,
										   String traceId) {
		return new ApiResponse<>(
				ApiStatus.ERROR,
				httpStatus.value(),
				message,
				null,
				errors,
				traceId
		);
	}

	/* ========== GETTERS ========== */

	public ApiStatus getStatus() { return status; }
	public int getCode() { return code; }
	public String getMessage() { return message; }
	public T getPayload() { return payload; }
	public List<String> getErrors() { return errors; }
	public Instant getTimestamp() { return timestamp; }
	public String getTraceId() { return traceId; }
}
