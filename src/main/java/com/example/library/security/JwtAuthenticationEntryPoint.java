package com.example.library.security;

import com.example.library.dto.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Entry point to handle unauthorized access attempts.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private final ObjectMapper mapper;

	/**
	 * Handles unauthorized requests.
	 *
	 * @param request       HttpServletRequest
	 * @param response      HttpServletResponse
	 * @param authException AuthenticationException
	 * @throws IOException if an input or output error occurs
	 */
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
						 AuthenticationException authException) throws IOException {

		// Log the exception
		System.out.println("AuthenticationEntryPoint: " + authException.getMessage());

		// Set the response status and content type
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType("application/json");

		// Create an ApiResponse
		ApiResponse<Object> apiResponse = ApiResponse.<Object>builder()
				.status(HttpStatus.UNAUTHORIZED.value())
				.message("You need to log in to access this resource.")
				.data(null)
				.timestamp(LocalDateTime.now())
				.build();

		// Write the ApiResponse as JSON
		response.getWriter().write(mapper.writeValueAsString(apiResponse));
	}
}
