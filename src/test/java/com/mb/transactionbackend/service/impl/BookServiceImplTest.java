package com.mb.transactionbackend.service.impl;

import com.mb.transactionbackend.dto.BookRegistrationRequest;
import com.mb.transactionbackend.dto.BookResponse;
import com.mb.transactionbackend.exception.ResourceNotFoundException;
import com.mb.transactionbackend.model.Book;
import com.mb.transactionbackend.repository.BookRepository;
import com.mb.transactionbackend.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book sampleBook;
    private BookRegistrationRequest registrationRequest;

    @BeforeEach
    void setUp() {
        sampleBook = Book.builder()
                .id(1L)
                .bookId("B001")
                .isbn("978-3-16-148410-0")
                .title("Refactoring")
                .author("Martin Fowler")
                .build();

        registrationRequest = new BookRegistrationRequest(
                "B001",
                "978-3-16-148410-0",
                "Refactoring",
                "Martin Fowler"
        );
    }

    @Test
    void registerBook_ShouldDelegateToRepository_andReturnSaved() {
        when(bookRepository.save(any(Book.class))).thenReturn(sampleBook);

        Book result = bookService.registerBook(registrationRequest);

        assertSame(sampleBook, result);

        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void listBooks_ShouldMapAllEntriesWithBorrowedFlag() {
        Book other = Book.builder()
                .id(2L)
                .bookId("B002")
                .isbn("978-0-13-235088-4")
                .title("Clean Code")
                .author("Robert C. Martin")
                .build();

        when(bookRepository.findAll()).thenReturn(List.of(sampleBook, other));
        when(loanRepository.existsByBookAndReturnedAtIsNull(sampleBook)).thenReturn(true);
        when(loanRepository.existsByBookAndReturnedAtIsNull(other)).thenReturn(false);

        List<BookResponse> responses = bookService.listBooks();

        assertEquals(2, responses.size());
        BookResponse r1 = responses.get(0);
        assertEquals("B001", r1.bookId());
        assertTrue(r1.borrowed());

        BookResponse r2 = responses.get(1);
        assertEquals("B002", r2.bookId());
        assertFalse(r2.borrowed());
    }

    @Test
    void findByBookId_WhenExists_ReturnsBook() {
        when(bookRepository.findByBookId("B001"))
                .thenReturn(Optional.of(sampleBook));

        Book b = bookService.findByBookId("B001");
        assertSame(sampleBook, b);
    }

    @Test
    void findByBookId_WhenMissing_ThrowsNotFound() {
        when(bookRepository.findByBookId("NOTFOUND"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> bookService.findByBookId("NOTFOUND"));
    }

    @Test
    void listBooks_WithPageable_ShouldReturnPage() {
        PageRequest pageReq = PageRequest.of(0, 5, Sort.by("title"));
        Page<Book> pageOfBooks = new PageImpl<>(List.of(sampleBook), pageReq, 1);
        when(bookRepository.findAll(pageReq)).thenReturn(pageOfBooks);
        when(loanRepository.existsByBookAndReturnedAtIsNull(sampleBook)).thenReturn(false);

        Page<BookResponse> page = bookService.listBooks(pageReq);

        assertEquals(1, page.getTotalElements());
        BookResponse resp = page.getContent().get(0);
        assertEquals("B001", resp.bookId());
        assertFalse(resp.borrowed());
    }
}
