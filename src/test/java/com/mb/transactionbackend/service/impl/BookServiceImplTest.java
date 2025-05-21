package com.mb.transactionbackend.service.impl;

import com.mb.transactionbackend.exception.ResourceNotFoundException;
import com.mb.transactionbackend.model.Book;
import com.mb.transactionbackend.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book testBook;

    @BeforeEach
    void setUp() {
        testBook = Book.builder()
                .id(1L)
                .bookId("B001")
                .isbn("978-3-16-148410-0")
                .title("Test Book")
                .author("Test Author")
                .build();
    }

    @Test
    void createBook_ShouldSaveAndReturnBook() {
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        Book result = bookService.registerBook(testBook);

        assertEquals(testBook, result);
        verify(bookRepository, times(1)).save(testBook);
    }

    @Test
    void findById_WhenBookExists_ShouldReturnBook() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        Book result = bookService.findById(1L);

        assertEquals(testBook, result);
    }

    @Test
    void findById_WhenBookDoesNotExist_ShouldThrowException() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.findById(99L));
    }

    @Test
    void findByBookId_WhenBookExists_ShouldReturnBook() {
        when(bookRepository.findByBookId("B001")).thenReturn(Optional.of(testBook));

        Book result = bookService.findByBookId("B001");

        assertEquals(testBook, result);
    }

    @Test
    void findByBookId_WhenBookDoesNotExist_ShouldThrowException() {
        when(bookRepository.findByBookId("NONEXISTENT")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.findByBookId("NONEXISTENT"));
    }

    @Test
    void findAllBooks_ShouldReturnAllBooks() {
        List<Book> books = Arrays.asList(
                testBook,
                Book.builder().id(2L).bookId("B002").isbn("978-3-16-148410-1").title("Another Book").author("Another Author").build()
        );

        when(bookRepository.findAll()).thenReturn(books);

        List<Book> result = bookService.findAllBooks();

        assertEquals(2, result.size());
        assertEquals(books, result);
    }

    @Test
    void updateBook_WhenBookExists_ShouldUpdateAndReturn() {
        Book updatedBook = Book.builder()
                .id(1L)
                .bookId("B001")
                .isbn("978-3-16-148410-0")
                .title("Updated Title")
                .author("Updated Author")
                .build();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(bookRepository.save(any(Book.class))).thenReturn(updatedBook);

        Book result = bookService.updateBook(1L, updatedBook);

        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Author", result.getAuthor());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void updateBook_WhenBookDoesNotExist_ShouldThrowException() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.updateBook(99L, testBook));
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void deleteBook_WhenBookExists_ShouldDeleteBook() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        doNothing().when(bookRepository).delete(testBook);

        bookService.deleteBook(1L);

        verify(bookRepository, times(1)).delete(testBook);
    }

    @Test
    void deleteBook_WhenBookDoesNotExist_ShouldThrowException() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.deleteBook(99L));
        verify(bookRepository, never()).delete(any(Book.class));
    }
}