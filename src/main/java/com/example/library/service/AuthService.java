package com.example.library.service;

import com.example.library.dto.request.BorrowerRequest;
import com.example.library.dto.request.LoginRequest;
import com.example.library.dto.response.BorrowerResponse;
import com.example.library.dto.response.JwtResponse;

/**
 * Service interface for authentication and user registration.
 */
public interface AuthService {
	JwtResponse authenticateUser(LoginRequest loginRequest);
	BorrowerResponse registerUser(BorrowerRequest borrowerRequest);
}
