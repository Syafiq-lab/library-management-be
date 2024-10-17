package com.example.library.controller;

import com.example.library.dto.request.BookRequest;
import com.example.library.dto.response.BookResponse;
import com.example.library.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for BookController.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class BookControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private BookService bookService;

	private BookResponse bookResponse;

	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach
	public void setUp() {
		bookResponse = new BookResponse();
		bookResponse.setBookId("book123");
		bookResponse.setTitle("Test Book");
		bookResponse.setAuthor("Author Name");
		bookResponse.setIsbn("123-456-789");
	}

	/**
	 * Test registering a book without authentication.
	 * Expected: 401 Unauthorized
	 */
	@Test
	public void registerBook_Unauthenticated_ShouldReturn401() throws Exception {
		BookRequest bookRequest = new BookRequest();
		bookRequest.setIsbn("123-456-789");
		bookRequest.setTitle("Test Book");
		bookRequest.setAuthor("Author Name");

		mockMvc.perform(post("/api/books")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(bookRequest)))
				.andExpect(status().isUnauthorized())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.status").value(401))
				.andExpect(jsonPath("$.message").value("You need to log in to access this resource."));
	}

	/**
	 * Test registering a book with authentication but without ROLE_ADMIN.
	 * Expected: 403 Forbidden
	 */
	@Test
	@WithMockUser(username = "user", roles = {"USER"})
	public void registerBook_AuthenticatedWithoutAdminRole_ShouldReturn403() throws Exception {
		BookRequest bookRequest = new BookRequest();
		bookRequest.setIsbn("123-456-789");
		bookRequest.setTitle("Test Book");
		bookRequest.setAuthor("Author Name");

		mockMvc.perform(post("/api/books")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(bookRequest)))
				.andExpect(status().isForbidden())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.status").value(403))
				.andExpect(jsonPath("$.message").value("Access is denied"));
	}

	/**
	 * Test registering a book with authentication and ROLE_ADMIN.
	 * Expected: 201 Created
	 */
	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	public void registerBook_AuthenticatedWithAdminRole_ShouldReturn201() throws Exception {
		BookRequest bookRequest = new BookRequest();
		bookRequest.setIsbn("123-456-789");
		bookRequest.setTitle("Test Book");
		bookRequest.setAuthor("Author Name");

		when(bookService.registerBook(any(BookRequest.class))).thenReturn(bookResponse);

		mockMvc.perform(post("/api/books")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(bookRequest)))
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.status").value(201))
				.andExpect(jsonPath("$.message").value("Book registered successfully"))
				.andExpect(jsonPath("$.data.title").value("Test Book"));

		verify(bookService, times(1)).registerBook(any(BookRequest.class));
	}

	// The rest of the test methods remain the same, as they don't involve BookRequest.

	/**
	 * Test borrowing a book without authentication.
	 * Expected: 401 Unauthorized
	 */
	@Test
	public void borrowBook_Unauthenticated_ShouldReturn401() throws Exception {
		String bookId = "book123";

		mockMvc.perform(post("/api/books/{bookId}/borrow", bookId))
				.andExpect(status().isUnauthorized())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.status").value(401))
				.andExpect(jsonPath("$.message").value("You need to log in to access this resource."));
	}

	// ... rest of the methods
}
