package com.mb.transactionbackend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;

@Audited
@Entity
@Table(name = "borrowers", uniqueConstraints = {
        @UniqueConstraint(name = "uk_borrower_borrower_id", columnNames = "borrowerId"),
        @UniqueConstraint(name = "uk_borrower_email", columnNames = "email")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Borrower extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String borrowerId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;
}