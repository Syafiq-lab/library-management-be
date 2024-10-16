package com.example.library.service.impl;

import com.example.library.dto.request.BorrowerRequest;
import com.example.library.dto.request.LoginRequest;
import com.example.library.dto.response.BorrowerResponse;
import com.example.library.dto.response.JwtResponse;
import com.example.library.exception.CustomException;
import com.example.library.mapper.BorrowerMapper;
import com.example.library.model.*;
import com.example.library.model.enums.RoleName;
import com.example.library.repository.*;
import com.example.library.security.JwtTokenProvider;
import com.example.library.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;

/**
 * Implementation of AuthService.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

	private final AuthenticationManager authenticationManager;
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final BorrowerRepository borrowerRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider tokenProvider;
	private final BorrowerMapper borrowerMapper;

	/**
	 * Authenticates a user and returns a JWT token.
	 *
	 * @param loginRequest LoginRequest containing username and password
	 * @return JwtResponse containing the JWT token
	 */
	@Override
	public JwtResponse authenticateUser(LoginRequest loginRequest) {
		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(
							loginRequest.getUsername(),
							loginRequest.getPassword()
					)
			);

			String jwt = tokenProvider.generateToken(authentication.getName());

			log.info("User '{}' authenticated successfully", loginRequest.getUsername());

			return new JwtResponse(jwt);
		} catch (AuthenticationException e) {
			log.error("Authentication failed for user '{}'", loginRequest.getUsername());
			throw new CustomException("Invalid username or password", HttpStatus.UNAUTHORIZED);
		}
	}

	/**
	 * Registers a new user.
	 *
	 * @param borrowerRequest BorrowerRequest containing user details
	 * @return BorrowerResponse with registered user information
	 */
	@Override
	public BorrowerResponse registerUser(BorrowerRequest borrowerRequest) {
		if (userRepository.existsByUsername(borrowerRequest.getUsername())) {
			log.error("Username '{}' is already taken", borrowerRequest.getUsername());
			throw new CustomException("Username is already taken!", HttpStatus.BAD_REQUEST);
		}

		if (borrowerRepository.existsByEmail(borrowerRequest.getEmail())) {
			log.error("Email '{}' is already in use", borrowerRequest.getEmail());
			throw new CustomException("Email is already in use!", HttpStatus.BAD_REQUEST);
		}

		// Create user account
		UserEntity user = UserEntity.builder()
				.username(borrowerRequest.getUsername())
				.password(passwordEncoder.encode(borrowerRequest.getPassword()))
				.roles(Collections.singleton(getUserRole()))
				.build();

		UserEntity savedUser = userRepository.save(user);

		// Create borrower profile
		BorrowerEntity borrower = borrowerMapper.toEntity(borrowerRequest);
		borrower.setUser(savedUser);
		borrower.setBorrowerId(UUID.randomUUID().toString());

		BorrowerEntity savedBorrower = borrowerRepository.save(borrower);

		log.info("User '{}' registered successfully", borrowerRequest.getUsername());

		return borrowerMapper.toResponse(savedBorrower);
	}

	/**
	 * Retrieves the USER role from the database.
	 *
	 * @return RoleEntity with ROLE_USER
	 */
	private RoleEntity getUserRole() {
		return roleRepository.findByName(RoleName.ROLE_USER)
				.orElseThrow(() -> new CustomException("User Role not set.", HttpStatus.INTERNAL_SERVER_ERROR));
	}
}
