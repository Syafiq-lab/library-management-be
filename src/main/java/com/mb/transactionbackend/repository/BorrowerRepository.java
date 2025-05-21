package com.mb.transactionbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mb.transactionbackend.model.Borrower;

import java.util.Optional;

public interface BorrowerRepository extends JpaRepository<Borrower, Long> {
    Optional<Borrower> findByBorrowerId(String borrowerId);
    boolean existsByBorrowerId(String borrowerId);
    boolean existsByEmail(String email);
}