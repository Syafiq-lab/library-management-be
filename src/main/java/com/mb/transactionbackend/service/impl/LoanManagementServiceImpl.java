package com.mb.transactionbackend.service.impl;

import com.mb.transactionbackend.dto.LoanRequest;
import com.mb.transactionbackend.model.Book;
import com.mb.transactionbackend.model.Borrower;
import com.mb.transactionbackend.model.Loan;
import com.mb.transactionbackend.repository.LoanRepository;
import com.mb.transactionbackend.service.BookService;
import com.mb.transactionbackend.service.BorrowerService;
import com.mb.transactionbackend.service.LoanManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LoanManagementServiceImpl implements LoanManagementService {

    private final LoanRepository loanRepository;
    private final BorrowerService borrowerService;
    private final BookService bookService;

    @Override
    public Loan borrowBook(LoanRequest request) {
        log.info("Processing borrow request for book ID: {} by borrower ID: {}", request.bookId(), request.borrowerId());
        
        Borrower borrower = borrowerService.findByBorrowerId(request.borrowerId());
        log.debug("Found borrower: {}", borrower.getName());
        
        Book book = bookService.findByBookId(request.bookId());
        log.debug("Found book: {}", book.getTitle());

        if (loanRepository.existsByBookAndReturnedAtIsNull(book)) {
            log.warn("Borrow request denied: Book ID {} is already borrowed", request.bookId());
            throw new IllegalStateException("Book is already borrowed");
        }

        Loan loan = Loan.builder()
                .borrower(borrower)
                .book(book)
                .borrowedAt(LocalDateTime.now())
                .build();
        
        Loan savedLoan = loanRepository.save(loan);
        log.info("Book ID: {} successfully borrowed by borrower ID: {}, loan ID: {}", 
                book.getBookId(), borrower.getBorrowerId(), savedLoan.getId());
        
        return savedLoan;
    }

    @Override
    @Transactional
    @CacheEvict(value = {"loansByUser", "loansByBook"}, allEntries = true)
    public Loan returnBook(LoanRequest request) {
        log.info("Processing return request for book ID: {} by borrower ID: {}", request.bookId(), request.borrowerId());
        
        Borrower borrower = borrowerService.findByBorrowerId(request.borrowerId());
        log.debug("Found borrower: {}", borrower.getName());
        
        Book book = bookService.findByBookId(request.bookId());
        log.debug("Found book: {}", book.getTitle());

        Loan loan = loanRepository.findByBorrowerAndBookAndReturnedAtIsNull(borrower, book)
                .orElseThrow(() -> {
                    log.error("Return request failed: No active loan found for borrower ID: {} and book ID: {}", 
                            borrower.getBorrowerId(), book.getBookId());
                    return new IllegalStateException("No active loan for this borrower/book");
                });

        loan.setReturnedAt(LocalDateTime.now());
        Loan savedLoan = loanRepository.save(loan);
        
        log.info("Book ID: {} successfully returned by borrower ID: {}, loan ID: {}", 
                book.getBookId(), borrower.getBorrowerId(), savedLoan.getId());
        
        return savedLoan;
    }
}