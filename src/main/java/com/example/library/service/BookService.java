package com.example.library.service;

import com.example.library.dto.request.BookRequest;
import com.example.library.dto.response.BookResponse;

import java.util.List;

/**
 * Service interface for managing books.
 */
public interface BookService {
	BookResponse registerBook(BookRequest bookRequest);
	List<BookResponse> listAllBooks();
	void borrowBook(String bookId);
	void returnBook(String bookId);
}
