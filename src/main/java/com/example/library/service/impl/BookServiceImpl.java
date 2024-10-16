package com.example.library.service.impl;

import com.example.library.dto.request.BookRequest;
import com.example.library.dto.response.BookResponse;
import com.example.library.exception.CustomException;
import com.example.library.mapper.BookMapper;
import com.example.library.model.*;
import com.example.library.repository.*;
import com.example.library.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of BookService.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BookServiceImpl implements BookService {

	private final BookRepository bookRepository;
	private final BorrowerRepository borrowerRepository;
	private final BookMapper bookMapper;

	/**
	 * Registers a new book in the system.
	 *
	 * @param bookRequest BookRequest containing book details
	 * @return BookResponse with registered book information
	 */
	@Override
	public BookResponse registerBook(BookRequest bookRequest) {
		if (bookRepository.existsByIsbnAndTitleAndAuthor(
				bookRequest.getIsbn(), bookRequest.getTitle(), bookRequest.getAuthor())) {
			log.error("Book with ISBN '{}', title '{}', and author '{}' already exists",
					bookRequest.getIsbn(), bookRequest.getTitle(), bookRequest.getAuthor());
			throw new CustomException("Book with the same ISBN, title, and author already exists", HttpStatus.BAD_REQUEST);
		}

		BookEntity bookEntity = bookMapper.toEntity(bookRequest);
		bookEntity.setBookId(UUID.randomUUID().toString());

		BookEntity savedBook = bookRepository.save(bookEntity);

		log.info("Book '{}' registered successfully", bookRequest.getTitle());

		return bookMapper.toResponse(savedBook);
	}

	/**
	 * Retrieves a list of all books.
	 *
	 * @return List of BookResponse instances
	 */
	@Override
	public List<BookResponse> listAllBooks() {
		List<BookResponse> books = bookRepository.findAll().stream()
				.map(bookMapper::toResponse)
				.collect(Collectors.toList());
		log.info("Retrieved {} books", books.size());
		return books;
	}

	/**
	 * Borrows a book for the authenticated user.
	 *
	 * @param bookId ID of the book to borrow
	 */
	@Override
	public void borrowBook(String bookId) {
		BookEntity book = bookRepository.findById(bookId)
				.orElseThrow(() -> new CustomException("Book not found", HttpStatus.NOT_FOUND));

		if (book.getBorrower() != null) {
			log.error("Book '{}' is already borrowed", book.getTitle());
			throw new CustomException("Book is already borrowed", HttpStatus.BAD_REQUEST);
		}

		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		BorrowerEntity borrower = borrowerRepository.findByUserUsername(username)
				.orElseThrow(() -> new CustomException("Borrower not found", HttpStatus.NOT_FOUND));

		book.setBorrower(borrower);
		bookRepository.save(book);

		log.info("User '{}' borrowed book '{}'", username, book.getTitle());
	}

	/**
	 * Returns a borrowed book for the authenticated user.
	 *
	 * @param bookId ID of the book to return
	 */
	@Override
	public void returnBook(String bookId) {
		BookEntity book = bookRepository.findById(bookId)
				.orElseThrow(() -> new CustomException("Book not found", HttpStatus.NOT_FOUND));

		if (book.getBorrower() == null) {
			log.error("Book '{}' is not currently borrowed", book.getTitle());
			throw new CustomException("Book is not borrowed", HttpStatus.BAD_REQUEST);
		}

		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		if (!book.getBorrower().getUser().getUsername().equals(username)) {
			log.error("User '{}' did not borrow book '{}'", username, book.getTitle());
			throw new CustomException("You did not borrow this book", HttpStatus.UNAUTHORIZED);
		}

		book.setBorrower(null);
		bookRepository.save(book);

		log.info("User '{}' returned book '{}'", username, book.getTitle());
	}
}
