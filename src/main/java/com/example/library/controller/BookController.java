package com.example.library.controller;

import com.example.library.dto.request.BookRequest;
import com.example.library.dto.response.*;
import com.example.library.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

/**
 * REST controller for managing books.
 */
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

	private final BookService bookService;

	/**
	 * Registers a new book. Only accessible by ADMIN role.
	 *
	 * @param bookRequest BookRequest containing book details
	 * @return ResponseEntity with registered book information
	 */
	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<BookResponse>> registerBook(
			@Valid @RequestBody BookRequest bookRequest) {
		BookResponse bookResponse = bookService.registerBook(bookRequest);
		ApiResponse<BookResponse> response = ApiResponse.<BookResponse>builder()
				.status(HttpStatus.CREATED.value())
				.message("Book registered successfully")
				.data(bookResponse)
				.timestamp(LocalDateTime.now())
				.build();
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	/**
	 * Retrieves a list of all books.
	 *
	 * @return ResponseEntity with a list of books
	 */
	@GetMapping
	public ResponseEntity<ApiResponse<List<BookResponse>>> listAllBooks() {
		List<BookResponse> books = bookService.listAllBooks();
		ApiResponse<List<BookResponse>> response = ApiResponse.<List<BookResponse>>builder()
				.status(HttpStatus.OK.value())
				.message("Books retrieved successfully")
				.data(books)
				.timestamp(LocalDateTime.now())
				.build();
		return ResponseEntity.ok(response);
	}

	/**
	 * Borrows a book for the authenticated user.
	 *
	 * @param bookId ID of the book to borrow
	 * @return ResponseEntity with success message
	 */
	@PostMapping("/{bookId}/borrow")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<ApiResponse<String>> borrowBook(@PathVariable String bookId) {
		bookService.borrowBook(bookId);
		ApiResponse<String> response = ApiResponse.<String>builder()
				.status(HttpStatus.OK.value())
				.message("Book borrowed successfully")
				.data(null)
				.timestamp(LocalDateTime.now())
				.build();
		return ResponseEntity.ok(response);
	}

	/**
	 * Returns a borrowed book for the authenticated user.
	 *
	 * @param bookId ID of the book to return
	 * @return ResponseEntity with success message
	 */
	@PostMapping("/{bookId}/return")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<ApiResponse<String>> returnBook(@PathVariable String bookId) {
		bookService.returnBook(bookId);
		ApiResponse<String> response = ApiResponse.<String>builder()
				.status(HttpStatus.OK.value())
				.message("Book returned successfully")
				.data(null)
				.timestamp(LocalDateTime.now())
				.build();
		return ResponseEntity.ok(response);
	}
}
