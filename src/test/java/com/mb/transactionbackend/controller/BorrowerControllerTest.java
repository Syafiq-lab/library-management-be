package com.mb.transactionbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mb.transactionbackend.dto.BorrowerRegistrationRequest;
import com.mb.transactionbackend.model.Borrower;
import com.mb.transactionbackend.service.BorrowerService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@WebMvcTest(BorrowerController.class)
@ExtendWith(MockitoExtension.class)
class BorrowerControllerTest {

	@Autowired
	private MockMvc mvc;
	@Autowired
	private ObjectMapper mapper;

	@MockBean
	private BorrowerService service;

	@Captor
	private ArgumentCaptor<BorrowerRegistrationRequest> captor;

	private Mockito.MockedStatic<LoggerFactory> loggerFactoryMock;
	private Logger fakeLogger;

	private BorrowerRegistrationRequest dto;
	private Borrower entity;

	@BeforeEach
	void init() {
		dto = new BorrowerRegistrationRequest(1L, "john@doe.com");
		entity = new Borrower();
		entity.setBorrowerId(1L);
		entity.setEmail(dto.email());

		lenient().when(service.registerBorrower(argThat(a -> a.borrowerId() == -1)))
				.thenThrow(new IllegalArgumentException("forced"));

		fakeLogger = mock(Logger.class);
		loggerFactoryMock = Mockito.mockStatic(LoggerFactory.class);
		loggerFactoryMock.when(() -> LoggerFactory.getLogger(any(Class.class)))
				.thenReturn(fakeLogger);
	}

	@AfterEach
	void cleanup() {
		if (loggerFactoryMock != null) {
			loggerFactoryMock.close();
		}
	}

	@Test
	@DisplayName("POST /api/borrowers – happy path with argument capture & InOrder verify")
	void shouldRegisterBorrower() throws Exception {
		when(service.registerBorrower(any())).thenReturn(entity);

		mvc.perform(post("/api/borrowers")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(dto)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.data.email", is(dto.email())));

		verify(service).registerBorrower(captor.capture());
		BorrowerRegistrationRequest actual = captor.getValue();
		assertEquals(dto.email(), actual.email());

		InOrder inOrder = inOrder(fakeLogger, service);
		inOrder.verify(fakeLogger).info(contains("Received request"), any(), any());
		inOrder.verify(service).registerBorrower(any());
	}

}