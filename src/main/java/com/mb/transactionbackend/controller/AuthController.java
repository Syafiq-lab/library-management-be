package com.mb.transactionbackend.controller;

import com.mb.transactionbackend.dto.ApiResponse;
import com.mb.transactionbackend.dto.AuthRequest;
import com.mb.transactionbackend.dto.AuthResponse;
import com.mb.transactionbackend.dto.UserRegistrationRequest;
import com.mb.transactionbackend.model.User;
import com.mb.transactionbackend.service.UserService;
import io.github.bucket4j.Bucket;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {

    private final UserService userService;
    private final Bucket tokenBucket;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    @Timed(value = "auth.register", description = "Time taken to register new user")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<User> register(@Valid @RequestBody UserRegistrationRequest request) {
        log.info("Received user registration request for username: {}", request.getUsername());
        
        try {
            assertNotRateLimited();
            User user = userService.registerUser(request);
            log.info("User registration successful for username: {}, user ID: {}", 
                    request.getUsername(), user.getId());
            return ApiResponse.success("User registered successfully", user);
        } catch (ResponseStatusException e) {
            log.warn("Rate limit exceeded for registration request");
            throw e;
        } catch (Exception e) {
            log.error("Failed to register user with username: {}: {}", 
                    request.getUsername(), e.getMessage());
            throw e;
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and get token")
    @Timed(value = "auth.login", description = "Time taken to login")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        log.info("Received login request for username: {}", request.getUsername());
        
        try {
            assertNotRateLimited();
            String token = userService.authenticateUser(request);
            log.info("Login successful for username: {}", request.getUsername());
            return ApiResponse.success("Login successful", new AuthResponse(token));
        } catch (ResponseStatusException e) {
            log.warn("Rate limit exceeded for login request");
            throw e;
        } catch (Exception e) {
            log.error("Login failed for username: {}: {}", 
                    request.getUsername(), e.getMessage());
            throw e;
        }
    }

    private void assertNotRateLimited() {
        if (!tokenBucket.tryConsume(1)) {
            log.warn("Rate limit exceeded for authentication request");
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests");
        }
    }
}