package com.example.library.dto.response;

import lombok.Data;

/**
 * Data Transfer Object for book responses.
 */
@Data
public class BookResponse {

	private String bookId;
	private String isbn;
	private String title;
	private String author;
	private String borrowerId; // Nullable if not borrowed
}
