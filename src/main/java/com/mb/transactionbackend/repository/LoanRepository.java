package com.mb.transactionbackend.repository;

import com.mb.transactionbackend.model.Book;
import com.mb.transactionbackend.model.Borrower;
import com.mb.transactionbackend.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    boolean existsByBookAndReturnedAtIsNull(Book book);
    Optional<Loan> findByBorrowerAndBookAndReturnedAtIsNull(Borrower borrower, Book book);
}