package com.example.library.controller;

import com.example.library.dto.request.*;
import com.example.library.dto.response.*;
import com.example.library.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.time.LocalDateTime;

/**
 * REST controller for authentication and user registration endpoints.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	/**
	 * Authenticates a user and returns a JWT token.
	 *
	 * @param loginRequest LoginRequest containing username and password
	 * @return ResponseEntity with JWT token
	 */
	@PostMapping("/signin")
	public ResponseEntity<ApiResponse<JwtResponse>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
		JwtResponse jwtResponse = authService.authenticateUser(loginRequest);
		ApiResponse<JwtResponse> response = ApiResponse.<JwtResponse>builder()
				.status(HttpStatus.OK.value())
				.message("User authenticated successfully")
				.data(jwtResponse)
				.timestamp(LocalDateTime.now())
				.build();
		return ResponseEntity.ok(response);
	}

	/**
	 * Registers a new user in the system.
	 *
	 * @param borrowerRequest BorrowerRequest containing user details
	 * @return ResponseEntity with registered user information
	 */
	@PostMapping("/signup")
	public ResponseEntity<ApiResponse<BorrowerResponse>> registerUser(@Valid @RequestBody BorrowerRequest borrowerRequest) {
		BorrowerResponse borrowerResponse = authService.registerUser(borrowerRequest);
		ApiResponse<BorrowerResponse> response = ApiResponse.<BorrowerResponse>builder()
				.status(HttpStatus.CREATED.value())
				.message("User registered successfully")
				.data(borrowerResponse)
				.timestamp(LocalDateTime.now())
				.build();
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
}
