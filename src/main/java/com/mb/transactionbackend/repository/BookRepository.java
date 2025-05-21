package com.mb.transactionbackend.repository;

import com.mb.transactionbackend.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByBookId(String bookId);
    List<Book> findByIsbn(String isbn);
    boolean existsByBookId(String bookId);
}