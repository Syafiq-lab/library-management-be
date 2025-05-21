package com.mb.transactionbackend.service.impl;

import com.mb.transactionbackend.dto.BorrowerRegistrationRequest;
import com.mb.transactionbackend.exception.ResourceNotFoundException;
import com.mb.transactionbackend.mapper.BorrowerMapper;
import com.mb.transactionbackend.model.Borrower;
import com.mb.transactionbackend.repository.BorrowerRepository;
import com.mb.transactionbackend.service.BorrowerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BorrowerServiceImpl implements BorrowerService {

    private final BorrowerRepository borrowerRepository;
    private final BorrowerMapper borrowerMapper;

    @Override
    public Borrower registerBorrower(BorrowerRegistrationRequest request) {
        log.info("Registering new borrower with ID: {}, email: {}", request.borrowerId(), request.email());
        
        if (borrowerRepository.existsByBorrowerId(request.borrowerId())) {
            log.warn("Registration failed: Borrower ID {} already exists", request.borrowerId());
            throw new IllegalArgumentException("Borrower ID already exists");
        }
        
        if (borrowerRepository.existsByEmail(request.email())) {
            log.warn("Registration failed: Email {} already exists", request.email());
            throw new IllegalArgumentException("Email already exists");
        }

        Borrower borrower = borrowerMapper.toEntity(request);
        Borrower savedBorrower = borrowerRepository.save(borrower);
        log.info("Borrower registered successfully with ID: {}", savedBorrower.getBorrowerId());
        
        return savedBorrower;
    }

    @Override
    public Borrower findByBorrowerId(String borrowerId) {
        log.info("Finding borrower with ID: {}", borrowerId);
        
        Borrower borrower = borrowerRepository.findByBorrowerId(borrowerId)
                .orElseThrow(() -> {
                    log.error("Borrower not found with ID: {}", borrowerId);
                    return new ResourceNotFoundException("Borrower not found");
                });
        
        log.debug("Found borrower: {}", borrower.getName());
        return borrower;
    }
}