package com.mb.transactionbackend.service;


import com.mb.transactionbackend.dto.BorrowerRegistrationRequest;
import com.mb.transactionbackend.model.Borrower;

public interface BorrowerService {
    Borrower registerBorrower(BorrowerRegistrationRequest request);
    Borrower findByBorrowerId(String borrowerId);
}