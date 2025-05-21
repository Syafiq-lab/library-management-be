package com.mb.transactionbackend.exception;

import com.mb.transactionbackend.web.TestController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
		controllers = TestController.class,
		excludeAutoConfiguration = {
				SecurityAutoConfiguration.class,
				SecurityFilterAutoConfiguration.class
		}
)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class GlobalExceptionHandlerTest {

	@Autowired MockMvc mvc;

	@Nested @DisplayName("Validation Exceptions")
	class ValidationTests {
		@Test
		void validationFailureReturns400AndFieldErrors() throws Exception {
			mvc.perform(post("/test/validate")
							.contentType("application/json")
							.content("{}"))
					.andExpect(status().isBadRequest())
					.andExpect(jsonPath("$.success").value(false))
					.andExpect(jsonPath("$.message").value("Validation failed"))
					.andExpect(jsonPath("$.data.field")
							.value(containsString("must not be blank")));
		}
	}

	@Test @DisplayName("BadCredentialsException → 401 Invalid credentials")
	void badCredentials() throws Exception {
		mvc.perform(get("/test/bad-credentials"))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.message").value("Invalid credentials"));
	}

	@Test @DisplayName("ExpiredJwtException → 401 JWT token has expired")
	void expiredJwt() throws Exception {
		mvc.perform(get("/test/expired-jwt"))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.message").value("JWT token has expired"));
	}

	@Test @DisplayName("ResourceNotFoundException → 404 no resource")
	void resourceNotFound() throws Exception {
		mvc.perform(get("/test/not-found"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("no resource"));
	}

	@Test @DisplayName("DuplicateResourceException → 409 exists")
	void duplicateResource() throws Exception {
		mvc.perform(get("/test/duplicate"))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.message").value("exists"));
	}

	@Test @DisplayName("UnauthorizedException → 401 no auth")
	void unauthorized() throws Exception {
		mvc.perform(get("/test/unauthorized"))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.message").value("no auth"));
	}

	@Test @DisplayName("AccessDeniedException → 403 Access denied")
	void accessDenied() throws Exception {
		mvc.perform(get("/test/access-denied"))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.message").value("Access denied"));
	}

	@Test @DisplayName("ResponseStatusException → 418 taste me")
	void teapot() throws Exception {
		mvc.perform(get("/test/teapot"))
				.andExpect(status().isIAmATeapot())
				.andExpect(jsonPath("$.message").value("taste me"));
	}

	@Test @DisplayName("Malformed JSON → 400 Malformed JSON request")
	void malformedJson() throws Exception {
		mvc.perform(post("/test/bad-json")
						.contentType("application/json")
						.content("{ \"unbalanced\": "))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Malformed JSON request"));
	}

	@Test @DisplayName("Unhandled Exception → 500 Unexpected error")
	void genericException() throws Exception {
		mvc.perform(get("/test/oops"))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.message").value("Unexpected error"));
	}
}
