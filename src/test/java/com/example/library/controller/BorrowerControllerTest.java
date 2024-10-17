package com.example.library.controller;

import com.example.library.dto.response.ApiResponse;
import com.example.library.dto.response.BorrowerResponse;
import com.example.library.exception.CustomException;
import com.example.library.service.BorrowerService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for BorrowerController.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class BorrowerControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private BorrowerService borrowerService;

	private BorrowerResponse borrowerResponse;

	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach
	public void setUp() {
		borrowerResponse = new BorrowerResponse();
		borrowerResponse.setBorrowerId("borrower123");
		borrowerResponse.setName("John Doe");
		// Add other necessary fields
	}

	/**
	 * Test retrieving borrower details without authentication.
	 * Expected: 401 Unauthorized
	 */
	@Test
	public void getBorrowerById_Unauthenticated_ShouldReturn401() throws Exception {
		String borrowerId = "borrower123";

		mockMvc.perform(get("/api/borrowers/{borrowerId}", borrowerId))
				.andExpect(status().isUnauthorized())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.status").value(401))
				.andExpect(jsonPath("$.message").value("You need to log in to access this resource."));
	}

	/**
	 * Test retrieving borrower details with authentication but without ROLE_ADMIN.
	 * Expected: 403 Forbidden
	 */
	@Test
	@WithMockUser(username = "user", roles = {"USER"})
	public void getBorrowerById_AuthenticatedWithoutAdminRole_ShouldReturn403() throws Exception {
		String borrowerId = "borrower123";

		mockMvc.perform(get("/api/borrowers/{borrowerId}", borrowerId))
				.andExpect(status().isForbidden())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.status").value(403))
				.andExpect(jsonPath("$.message").value("Access is denied"));
	}

	/**
	 * Test retrieving borrower details with authentication and ROLE_ADMIN.
	 * Expected: 200 OK
	 */
	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	public void getBorrowerById_AuthenticatedWithAdminRole_ShouldReturn200() throws Exception {
		String borrowerId = "borrower123";
		when(borrowerService.getBorrowerById(borrowerId)).thenReturn(borrowerResponse);

		mockMvc.perform(get("/api/borrowers/{borrowerId}", borrowerId))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.status").value(200))
				.andExpect(jsonPath("$.message").value("Borrower retrieved successfully"))
				.andExpect(jsonPath("$.data.name").value("John Doe"));

		verify(borrowerService, times(1)).getBorrowerById(borrowerId);
	}

	/**
	 * Test retrieving borrower details when borrower is not found.
	 * Expected: 404 Not Found
	 */
	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	public void getBorrowerById_BorrowerNotFound_ShouldReturn404() throws Exception {
		String borrowerId = "nonexistentId";
		when(borrowerService.getBorrowerById(borrowerId))
				.thenThrow(new CustomException("Borrower not found", HttpStatus.NOT_FOUND));

		mockMvc.perform(get("/api/borrowers/{borrowerId}", borrowerId))
				.andExpect(status().isNotFound())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.message").value("Borrower not found"));

		verify(borrowerService, times(1)).getBorrowerById(borrowerId);
	}
}
