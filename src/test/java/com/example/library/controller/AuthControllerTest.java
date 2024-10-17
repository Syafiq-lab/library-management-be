package com.example.library.controller;

import com.example.library.dto.request.BorrowerRequest;
import com.example.library.dto.request.LoginRequest;
import com.example.library.dto.response.BorrowerResponse;
import com.example.library.dto.response.JwtResponse;
import com.example.library.exception.CustomException;
import com.example.library.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AuthService authService;

	@Autowired
	private ObjectMapper objectMapper;

	private JwtResponse jwtResponse;
	private BorrowerResponse borrowerResponse;

	@BeforeEach
	public void setUp() {
		jwtResponse = new JwtResponse("test-jwt-token");

		borrowerResponse = new BorrowerResponse();
		borrowerResponse.setBorrowerId("borrower123");
		borrowerResponse.setName("John Doe");
		// Set other necessary fields for BorrowerResponse
	}

	/**
	 * Test authenticating user with valid credentials.
	 * Expected: 200 OK
	 */
	@Test
	public void authenticateUser_ValidCredentials_ShouldReturn200() throws Exception {
		when(authService.authenticateUser(any(LoginRequest.class))).thenReturn(jwtResponse);

		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setUsername("admin");
		loginRequest.setPassword("password123");

		mockMvc.perform(post("/api/auth/signin")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(loginRequest)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.status").value(200))
				.andExpect(jsonPath("$.message").value("User authenticated successfully"))
				.andExpect(jsonPath("$.data.token").value("test-jwt-token"));
	}

	/**
	 * Test authenticating user with invalid credentials.
	 * Expected: 401 Unauthorized
	 */
	@Test
	public void authenticateUser_InvalidCredentials_ShouldReturn401() throws Exception {
		when(authService.authenticateUser(any(LoginRequest.class)))
				.thenThrow(new CustomException("Invalid username or password", HttpStatus.UNAUTHORIZED));

		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setUsername("admin");
		loginRequest.setPassword("wrongpassword");

		mockMvc.perform(post("/api/auth/signin")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(loginRequest)))
				.andExpect(status().isUnauthorized())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.status").value(401))
				.andExpect(jsonPath("$.message").value("Invalid username or password"));
	}

	/**
	 * Test registering a new user with valid details.
	 * Expected: 201 Created
	 */
	@Test
	public void registerUser_ValidRequest_ShouldReturn201() throws Exception {
		when(authService.registerUser(any(BorrowerRequest.class))).thenReturn(borrowerResponse);

		BorrowerRequest borrowerRequest = new BorrowerRequest();
		borrowerRequest.setUsername("newuser");
		borrowerRequest.setPassword("password123");
		borrowerRequest.setEmail("newuser@example.com");
		borrowerRequest.setName("New User");
		// Set other necessary fields for BorrowerRequest

		mockMvc.perform(post("/api/auth/signup")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(borrowerRequest)))
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.status").value(201))
				.andExpect(jsonPath("$.message").value("User registered successfully"))
				.andExpect(jsonPath("$.data.name").value("John Doe"));
	}

	/**
	 * Test registering a user with an existing username.
	 * Expected: 400 Bad Request
	 */
	@Test
	public void registerUser_UsernameAlreadyExists_ShouldReturn400() throws Exception {
		when(authService.registerUser(any(BorrowerRequest.class)))
				.thenThrow(new CustomException("Username is already taken!", HttpStatus.BAD_REQUEST));

		BorrowerRequest borrowerRequest = new BorrowerRequest();
		borrowerRequest.setUsername("existinguser");
		borrowerRequest.setPassword("password123");
		borrowerRequest.setEmail("user@example.com");
		borrowerRequest.setName("Existing User");
		// Set other necessary fields

		mockMvc.perform(post("/api/auth/signup")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(borrowerRequest)))
				.andExpect(status().isBadRequest())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.message").value("Username is already taken!"));
	}

	/**
	 * Test registering a user with an existing email.
	 * Expected: 400 Bad Request
	 */
	@Test
	public void registerUser_EmailAlreadyInUse_ShouldReturn400() throws Exception {
		when(authService.registerUser(any(BorrowerRequest.class)))
				.thenThrow(new CustomException("Email is already in use!", HttpStatus.BAD_REQUEST));

		BorrowerRequest borrowerRequest = new BorrowerRequest();
		borrowerRequest.setUsername("newuser");
		borrowerRequest.setPassword("password123");
		borrowerRequest.setEmail("existingemail@example.com");
		borrowerRequest.setName("New User");
		// Set other necessary fields

		mockMvc.perform(post("/api/auth/signup")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(borrowerRequest)))
				.andExpect(status().isBadRequest())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.message").value("Email is already in use!"));
	}
}
