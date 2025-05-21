package com.mb.transactionbackend.controller;

import com.mb.transactionbackend.dto.ApiResponse;
import com.mb.transactionbackend.dto.LoanRequest;
import com.mb.transactionbackend.model.Loan;
import com.mb.transactionbackend.service.LoanManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanManagementService loanService;

    @PostMapping("/borrow")
    public ResponseEntity<ApiResponse<Loan>> borrowBook(@Valid @RequestBody LoanRequest request) {
        log.info("Received borrow request for book ID: {} by borrower ID: {}", request.bookId(), request.borrowerId());
        
        try {
            Loan loan = loanService.borrowBook(request);
            log.info("Successfully processed borrow request for book ID: {} by borrower ID: {}", 
                    request.bookId(), request.borrowerId());
            return ResponseEntity.ok(ApiResponse.success("Book borrowed", loan));
        } catch (Exception e) {
            log.error("Failed to process borrow request for book ID: {} by borrower ID: {}: {}", 
                    request.bookId(), request.borrowerId(), e.getMessage());
            throw e;
        }
    }

    @PostMapping("/return")
    public ResponseEntity<ApiResponse<Loan>> returnBook(@Valid @RequestBody LoanRequest request) {
        log.info("Received return request for book ID: {} by borrower ID: {}", request.bookId(), request.borrowerId());
        
        try {
            Loan loan = loanService.returnBook(request);
            log.info("Successfully processed return request for book ID: {} by borrower ID: {}", 
                    request.bookId(), request.borrowerId());
            return ResponseEntity.ok(ApiResponse.success("Book returned", loan));
        } catch (Exception e) {
            log.error("Failed to process return request for book ID: {} by borrower ID: {}: {}", 
                    request.bookId(), request.borrowerId(), e.getMessage());
            throw e;
        }
    }
}