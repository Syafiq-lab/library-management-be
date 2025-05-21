package com.mb.transactionbackend.service;


import com.mb.transactionbackend.dto.BookRegistrationRequest;
import com.mb.transactionbackend.dto.BookResponse;
import com.mb.transactionbackend.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookService {
    Book registerBook(BookRegistrationRequest request);
    List<BookResponse> listBooks();
    Book findByBookId(String bookId);
    Page<BookResponse> listBooks(Pageable pageable);
}