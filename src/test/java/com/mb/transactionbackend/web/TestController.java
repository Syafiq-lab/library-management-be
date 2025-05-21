package com.mb.transactionbackend.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {

	// 1) Validation error
	public record ValidDto(@NotBlank String field) {}

	@PostMapping("/validate")
	public void validate(@Valid @RequestBody ValidDto dto) { }

	// 2) Bad credentials
	@GetMapping("/bad-credentials")
	public void badCredentials() {
		throw new BadCredentialsException("bad creds");
	}

	// 3) Expired JWT
	@GetMapping("/expired-jwt")
	public void expiredJwt() {
		throw new io.jsonwebtoken.ExpiredJwtException(null, null, "expired");
	}

	// 4) Resource not found
	@GetMapping("/not-found")
	public void notFound() {
		throw new com.mb.transactionbackend.exception.ResourceNotFoundException("no resource");
	}

	// 5) Duplicate resource
	@GetMapping("/duplicate")
	public void duplicate() {
		throw new com.mb.transactionbackend.exception.DuplicateResourceException("exists");
	}

	// 6) Unauthorized
	@GetMapping("/unauthorized")
	public void unauthorized() {
		throw new com.mb.transactionbackend.exception.UnauthorizedException("no auth");
	}

	// 7) Access denied
	@GetMapping("/access-denied")
	public void accessDenied() {
		throw new AccessDeniedException("forbidden");
	}

	// 8) Teapot
	@GetMapping("/teapot")
	public void teapot() {
		throw new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT, "taste me");
	}

	// 9) Malformed JSON
	@PostMapping("/bad-json")
	public void badJson(@RequestBody Map<String, String> body) { }

	// 10) Generic exception
	@GetMapping("/oops")
	public void oops() {
		throw new RuntimeException("boom");
	}
}
