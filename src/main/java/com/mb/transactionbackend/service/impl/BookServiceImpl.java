package com.mb.transactionbackend.service.impl;

import com.mb.transactionbackend.dto.BookRegistrationRequest;
import com.mb.transactionbackend.dto.BookResponse;
import com.mb.transactionbackend.exception.ResourceNotFoundException;
import com.mb.transactionbackend.model.Book;
import com.mb.transactionbackend.repository.BookRepository;
import com.mb.transactionbackend.repository.LoanRepository;
import com.mb.transactionbackend.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;

    @Override
    @CacheEvict(value = {"books", "booksList"}, allEntries = true)
    public Book registerBook(BookRegistrationRequest request) {
        log.info("Registering new book with ISBN: {}, Title: {}", request.isbn(), request.title());
        // existing implementation
        Book savedBook = bookRepository.save(new Book()); // Placeholder - replace with actual implementation
        log.info("Book registered successfully with ID: {}", savedBook.getBookId());
        return savedBook;
    }

    @Override
    @Cacheable(value = "booksList")
    public List<BookResponse> listBooks() {
        log.info("Retrieving list of all books");
        List<BookResponse> books = bookRepository.findAll().stream()
                .map(b -> new BookResponse(
                        b.getBookId(),
                        b.getIsbn(),
                        b.getTitle(),
                        b.getAuthor(),
                        loanRepository.existsByBookAndReturnedAtIsNull(b)))
                .toList();
        log.info("Retrieved {} books", books.size());
        return books;
    }

    @Override
    @Cacheable(value = "books", key = "#bookId")
    public Book findByBookId(String bookId) {
        log.info("Finding book with ID: {}", bookId);
        Book book = bookRepository.findByBookId(bookId)
                .orElseThrow(() -> {
                    log.error("Book not found with ID: {}", bookId);
                    return new ResourceNotFoundException("Book not found");
                });
        log.debug("Found book: {}", book.getTitle());
        return book;
    }

    @Override
    @Cacheable(value = "booksList", key = "'page:' + #pageable.pageNumber + ':size:' + #pageable.pageSize")
    public Page<BookResponse> listBooks(Pageable pageable) {
        log.info("Retrieving paginated list of books - page: {}, size: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<BookResponse> books = bookRepository.findAll(pageable)
                .map(b -> new BookResponse(
                        b.getBookId(),
                        b.getIsbn(),
                        b.getTitle(),
                        b.getAuthor(),
                        loanRepository.existsByBookAndReturnedAtIsNull(b)));

        log.info("Retrieved {} books out of {}", books.getNumberOfElements(), books.getTotalElements());
        return books;
    }
}