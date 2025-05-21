package com.mb.transactionbackend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Audited
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "books", uniqueConstraints = {
        @UniqueConstraint(name = "uk_book_book_id", columnNames = "bookId")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Book extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String bookId;

    @Column(nullable = false)
    private String isbn;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

}