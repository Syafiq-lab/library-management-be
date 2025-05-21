package com.mb.transactionbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mb.transactionbackend.dto.LoanRequest;
import com.mb.transactionbackend.model.Book;
import com.mb.transactionbackend.model.Borrower;
import com.mb.transactionbackend.model.Loan;
import com.mb.transactionbackend.service.LoanManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;

@WebMvcTest(LoanController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class LoanControllerTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper mapper;

	@MockBean
	private LoanManagementService service;

	private LoanRequest request;
	private Loan loan;

	@BeforeEach
	void init() {
		request = new LoanRequest("10", "1");

		loan = new Loan();
		loan.setId(200L);

		Borrower borrower = new Borrower();
		borrower.setBorrowerId(request.borrowerId());
		loan.setBorrower(borrower);

		Book book = new Book();
		book.setBookId(request.bookId());
		loan.setBook(book);
	}

	@Test
	@DisplayName("Borrow & Return endpoints – verify within timeout")
	void borrowAndReturn() throws Exception {
		when(service.borrowBook(request)).thenReturn(loan);
		when(service.returnBook(request)).thenReturn(loan);

		mvc.perform(post("/api/loans/borrow")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.id", is(200)));

		verify(service, timeout(50)).borrowBook(request);

		mvc.perform(post("/api/loans/return")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.book.bookId", is("1")));

		verify(service, timeout(50)).returnBook(request);
	}
}
