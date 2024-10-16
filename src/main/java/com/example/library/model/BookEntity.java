package com.example.library.model;

import lombok.*;
import javax.persistence.*;

/**
 * Entity class representing a book.
 */
@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookEntity {

	@Id
	@Column(nullable = false, unique = true)
	private String bookId;

	@Column(nullable = false)
	private String isbn;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private String author;

	/**
	 * The borrower who has borrowed the book. Nullable if not borrowed.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "borrower_id")
	private BorrowerEntity borrower;
}
