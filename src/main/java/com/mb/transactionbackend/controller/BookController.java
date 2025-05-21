package com.mb.transactionbackend.controller;

import com.mb.transactionbackend.dto.BookRegistrationRequest;
import com.mb.transactionbackend.dto.BookResponse;
import com.mb.transactionbackend.model.Book;
import com.mb.transactionbackend.service.BookService;
import com.mb.transactionbackend.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping
    public ResponseEntity<ApiResponse<Book>> registerBook(
            @Valid @RequestBody BookRegistrationRequest request) {
        log.info("Received request to register book: {}, ISBN: {}", 
                request.title(), request.isbn());
        
        Book book = bookService.registerBook(request);
        log.info("Successfully registered book with ID: {}, title: {}", 
                book.getBookId(), book.getTitle());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Book registered", book));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<BookResponse>>> listBooks() {
        log.info("Received request to list all books");
        
        List<BookResponse> books = bookService.listBooks();
        log.info("Successfully retrieved {} books", books.size());
        return ResponseEntity.ok(ApiResponse.success("Books retrieved", books));
    }

    @GetMapping("/page")
    public ResponseEntity<ApiResponse<Page<BookResponse>>> listBooksByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        log.info("Received request to list books - page: {}, size: {}, sortBy: {}, direction: {}",
                page, size, sortBy, direction);

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<BookResponse> books = bookService.listBooks(pageable);
        log.info("Successfully retrieved {} books out of {}",
                books.getNumberOfElements(), books.getTotalElements());
        return ResponseEntity.ok(ApiResponse.success("Books retrieved", books));
    }
}