package com.example.library.security;

import com.example.library.dto.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Custom handler for Access Denied exceptions.
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

	private final ObjectMapper objectMapper;

	// Constructor injection
	public CustomAccessDeniedHandler(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	/**
	 * Handles access denied exceptions.
	 *
	 * @param request  HttpServletRequest
	 * @param response HttpServletResponse
	 * @param accessDeniedException AccessDeniedException
	 * @throws IOException if an input or output error occurs
	 */
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
					   AccessDeniedException accessDeniedException) throws IOException {

		// Log the exception
		System.out.println("CustomAccessDeniedHandler: " + accessDeniedException.getMessage());

		// Set the response status and content type
		response.setStatus(HttpStatus.FORBIDDEN.value());
		response.setContentType("application/json");

		// Create an ApiResponse
		ApiResponse<Object> apiResponse = ApiResponse.<Object>builder()
				.status(HttpStatus.FORBIDDEN.value())
				.message("You do not have permission to access this resource.")
				.data(null)
				.timestamp(LocalDateTime.now())
				.build();

		// Write the ApiResponse as JSON
		response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
	}
}
