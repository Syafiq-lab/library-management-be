package com.mb.transactionbackend.service;


import com.mb.transactionbackend.dto.LoanRequest;
import com.mb.transactionbackend.model.Loan;

public interface LoanManagementService {
    Loan borrowBook(LoanRequest request);
    Loan returnBook(LoanRequest request);
}