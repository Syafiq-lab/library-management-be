package com.mb.transactionbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mb.transactionbackend.dto.LoanRequest;
import com.mb.transactionbackend.model.Loan;
import com.mb.transactionbackend.service.LoanManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoanController.class)
class LoanControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @MockBean LoanManagementService service;

    LoanRequest request;
    Loan loan;

    @BeforeEach
    void init() {
        request = new LoanRequest(10L, 1L);          // Kotlin-style record / Java 23 record works too
        loan = new Loan(); loan.setLoanId(200L);
        loan.setBookId(request.bookId()); loan.setBorrowerId(request.borrowerId());
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
           .andExpect(jsonPath("$.data.loanId", is(200)));

        // We expect service to have been called quickly (< 50 ms).
        Mockito.verify(service, timeout(50)).borrowBook(request);

        mvc.perform(post("/api/loans/return")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.data.bookId", is(10)));

        Mockito.verify(service, timeout(50)).returnBook(request);
    }
}