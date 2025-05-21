package com.mb.transactionbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mb.transactionbackend.dto.BorrowerRegistrationRequest;
import com.mb.transactionbackend.model.Borrower;
import com.mb.transactionbackend.service.BorrowerService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@WebMvcTest(BorrowerController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class BorrowerControllerTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper mapper;

	@MockBean
	private BorrowerService service;

	@Captor
	private ArgumentCaptor<BorrowerRegistrationRequest> captor;

	private BorrowerRegistrationRequest dto;
	private Borrower entity;

	@BeforeEach
	void init() {
		dto = new BorrowerRegistrationRequest("1", "John Doe", "john@doe.com");
		entity = new Borrower();
		entity.setBorrowerId("1");
		entity.setEmail(dto.email());

		doThrow(new IllegalArgumentException("forced"))
				.when(service)
				.registerBorrower(argThat(a ->
						a != null && "-1".equals(a.borrowerId())
				));
	}

	@Test
	@DisplayName("POST /api/borrowers – happy path with argument capture")
	void shouldRegisterBorrower() throws Exception {
		when(service.registerBorrower(any())).thenReturn(entity);

		mvc.perform(post("/api/borrowers")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(dto)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.data.email", is(dto.email())));

		verify(service, times(1)).registerBorrower(captor.capture());
		BorrowerRegistrationRequest actual = captor.getValue();
		assertEquals(dto.email(), actual.email());
	}
}
