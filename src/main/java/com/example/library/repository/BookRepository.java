package com.example.library.repository;

import com.example.library.model.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for BookEntity.
 */
@Repository
public interface BookRepository extends JpaRepository<BookEntity, String> {

	/**
	 * Checks if a book exists with the same ISBN, title, and author.
	 *
	 * @param isbn   ISBN of the book
	 * @param title  Title of the book
	 * @param author Author of the book
	 * @return true if such a book exists, false otherwise
	 */
	boolean existsByIsbnAndTitleAndAuthor(String isbn, String title, String author);

	/**
	 * Checks if another book exists with the same ISBN but different ID.
	 *
	 * @param isbn   ISBN of the book
	 * @param bookId ID of the book to exclude
	 * @return true if such a book exists, false otherwise
	 */
	boolean existsByIsbnAndBookIdNot(String isbn, String bookId);
}
