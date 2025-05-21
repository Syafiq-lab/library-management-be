package com.mb.transactionbackend.service.impl;

import com.mb.transactionbackend.dto.LoanRequest;
import com.mb.transactionbackend.model.Book;
import com.mb.transactionbackend.model.Borrower;
import com.mb.transactionbackend.model.Loan;
import com.mb.transactionbackend.repository.LoanRepository;
import com.mb.transactionbackend.service.BookService;
import com.mb.transactionbackend.service.BorrowerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanManagementServiceImplTest {

    @Mock private LoanRepository loanRepository;
    @Mock private BookService bookService;
    @Mock private BorrowerService borrowerService;

    @InjectMocks private LoanManagementServiceImpl loanService;

    private Book book;
    private Borrower borrower;
    private Loan existingLoan;
    private LoanRequest request;

    @BeforeEach
    void setUp() {
        book = Book.builder()
                .id(1L)
                .bookId("B001")
                .title("Domain-Driven Design")
                .author("Eric Evans")
                .build();

        borrower = Borrower.builder()
                .id(1L)
                .borrowerId("U123")
                .name("Alice")
                .email("alice@example.com")
                .build();

        existingLoan = Loan.builder()
                .id(10L)
                .book(book)
                .borrower(borrower)
                .borrowedAt(LocalDateTime.now().minusDays(5))
                .build();

        request = new LoanRequest(borrower.getBorrowerId(), book.getBookId());
    }

    @Test
    void borrowBook_success() {
        when(borrowerService.findByBorrowerId("U123")).thenReturn(borrower);
        when(bookService.findByBookId("B001")).thenReturn(book);
        when(loanRepository.existsByBookAndReturnedAtIsNull(book)).thenReturn(false);
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Loan loan = loanService.borrowBook(request);

        assertNotNull(loan.getBorrowedAt(), "borrowedAt must be set");
        assertEquals(borrower, loan.getBorrower());
        assertEquals(book, loan.getBook());
        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    void borrowBook_whenAlreadyBorrowed_throws() {
        when(borrowerService.findByBorrowerId("U123")).thenReturn(borrower);
        when(bookService.findByBookId("B001")).thenReturn(book);
        when(loanRepository.existsByBookAndReturnedAtIsNull(book)).thenReturn(true);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> loanService.borrowBook(request)
        );
        assertEquals("Book is already borrowed", ex.getMessage());
        verify(loanRepository, never()).save(any());
    }

    @Test
    void returnBook_success() {
        // Simulate an active loan found
        when(borrowerService.findByBorrowerId("U123")).thenReturn(borrower);
        when(bookService.findByBookId("B001")).thenReturn(book);
        when(loanRepository.findByBorrowerAndBookAndReturnedAtIsNull(borrower, book))
                .thenReturn(Optional.of(existingLoan));
        when(loanRepository.save(existingLoan)).thenReturn(existingLoan);

        Loan returned = loanService.returnBook(request);

        assertNotNull(returned.getReturnedAt(), "returnedAt must be set");
        verify(loanRepository).save(existingLoan);
    }

    @Test
    void returnBook_whenNoActiveLoan_throws() {
        when(borrowerService.findByBorrowerId("U123")).thenReturn(borrower);
        when(bookService.findByBookId("B001")).thenReturn(book);
        when(loanRepository.findByBorrowerAndBookAndReturnedAtIsNull(borrower, book))
                .thenReturn(Optional.empty());

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> loanService.returnBook(request)
        );
        assertEquals("No active loan for this borrower/book", ex.getMessage());
        verify(loanRepository, never()).save(any());
    }
}
