package com.example.library.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Generic API response wrapper.
 *
 * @param <T> Type of the data payload
 */
@Data
@Builder
public class ApiResponse<T> {
	private int status;
	private String message;
	private T data;
	private LocalDateTime timestamp;
}
