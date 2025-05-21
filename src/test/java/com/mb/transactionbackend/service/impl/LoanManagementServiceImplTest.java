package com.mb.transactionbackend.service.impl;

import com.mb.transactionbackend.exception.ResourceNotFoundException;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanManagementServiceImplTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private BookService bookService;

    @Mock
    private BorrowerService borrowerService;

    @InjectMocks
    private LoanManagementServiceImpl loanManagementService;

    private Book testBook;
    private Borrower testBorrower;
    private Loan testLoan;

    @BeforeEach
    void setUp() {
        testBook = Book.builder()
                .id(1L)
                .bookId("B001")
                .isbn("978-3-16-148410-0")
                .title("Test Book")
                .author("Test Author")
                .build();

        testBorrower = Borrower.builder()
                .id(1L)
                .borrowerId("BOR001")
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        testLoan = Loan.builder()
                .id(1L)
                .book(testBook)
                .borrower(testBorrower)
                .borrowDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(14))
                .returned(false)
                .build();
    }

    @Test
    void borrowBook_ShouldCreateLoanAndReturn() {
        when(bookService.findById(1L)).thenReturn(testBook);
        when(borrowerService.findById(1L)).thenReturn(testBorrower);
        when(loanRepository.save(any(Loan.class))).thenReturn(testLoan);

        Loan result = loanManagementService.borrowBook(1L, 1L, LocalDate.now().plusDays(14));

        assertEquals(testLoan, result);
        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    void returnBook_WhenLoanExistsAndNotReturned_ShouldMarkAsReturned() {
        when(loanRepository.findById(1L)).thenReturn(Optional.of(testLoan));
        when(loanRepository.save(any(Loan.class))).thenReturn(testLoan);

        Loan result = loanManagementService.returnBook(1L);

        assertTrue(result.isReturned());
        verify(loanRepository, times(1)).save(testLoan);
    }

    @Test
    void returnBook_WhenLoanDoesNotExist_ShouldThrowException() {
        when(loanRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> loanManagementService.returnBook(99L));
        verify(loanRepository, never()).save(any(Loan.class));
    }

    @Test
    void findById_WhenLoanExists_ShouldReturnLoan() {
        when(loanRepository.findById(1L)).thenReturn(Optional.of(testLoan));

        Loan result = loanManagementService.findById(1L);

        assertEquals(testLoan, result);
    }

    @Test
    void findById_WhenLoanDoesNotExist_ShouldThrowException() {
        when(loanRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> loanManagementService.findById(99L));
    }

    @Test
    void findAllLoans_ShouldReturnAllLoans() {
        List<Loan> loans = Arrays.asList(
                testLoan,
                Loan.builder()
                        .id(2L)
                        .book(testBook)
                        .borrower(testBorrower)
                        .borrowDate(LocalDate.now().minusDays(7))
                        .dueDate(LocalDate.now().plusDays(7))
                        .returned(false)
                        .build()
        );

        when(loanRepository.findAll()).thenReturn(loans);

        List<Loan> result = loanManagementService.findAllLoans();

        assertEquals(2, result.size());
        assertEquals(loans, result);
    }

    @Test
    void findActiveLoans_ShouldReturnOnlyActiveLoans() {
        List<Loan> activeLoans = Arrays.asList(
                testLoan,
                Loan.builder()
                        .id(2L)
                        .book(testBook)
                        .borrower(testBorrower)
                        .borrowDate(LocalDate.now().minusDays(7))
                        .dueDate(LocalDate.now().plusDays(7))
                        .returned(false)
                        .build()
        );

        when(loanRepository.findByReturnedFalse()).thenReturn(activeLoans);

        List<Loan> result = loanManagementService.findActiveLoans();

        assertEquals(2, result.size());
        assertEquals(activeLoans, result);
    }

    @Test
    void findOverdueLoans_ShouldReturnOnlyOverdueLoans() {
        List<Loan> overdueLoans = Arrays.asList(
                Loan.builder()
                        .id(3L)
                        .book(testBook)
                        .borrower(testBorrower)
                        .borrowDate(LocalDate.now().minusDays(21))
                        .dueDate(LocalDate.now().minusDays(7))
                        .returned(false)
                        .build()
        );

        when(loanRepository.findByReturnedFalseAndDueDateBefore(any(LocalDate.class))).thenReturn(overdueLoans);

        List<Loan> result = loanManagementService.findOverdueLoans();

        assertEquals(1, result.size());
        assertEquals(overdueLoans, result);
    }

    @Test
    void findLoansByBorrower_ShouldReturnBorrowerLoans() {
        List<Loan> borrowerLoans = Arrays.asList(testLoan);

        when(borrowerService.findById(1L)).thenReturn(testBorrower);
        when(loanRepository.findByBorrower(testBorrower)).thenReturn(borrowerLoans);

        List<Loan> result = loanManagementService.findLoansByBorrower(1L);

        assertEquals(1, result.size());
        assertEquals(borrowerLoans, result);
    }

    @Test
    void findLoansByBook_ShouldReturnBookLoans() {
        List<Loan> bookLoans = Arrays.asList(testLoan);

        when(bookService.findById(1L)).thenReturn(testBook);
        when(loanRepository.findByBook(testBook)).thenReturn(bookLoans);

        List<Loan> result = loanManagementService.findLoansByBook(1L);

        assertEquals(1, result.size());
        assertEquals(bookLoans, result);
    }
}