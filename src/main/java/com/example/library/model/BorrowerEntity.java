package com.example.library.model;

import lombok.*;
import javax.persistence.*;
import java.util.Set;

/**
 * Entity class representing a borrower.
 */
@Entity
@Table(name = "borrowers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowerEntity {

	@Id
	@Column(nullable = false, unique = true)
	private String borrowerId;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false, unique = true)
	private String email;

	/**
	 * The books borrowed by the borrower.
	 */
	@OneToMany(mappedBy = "borrower", fetch = FetchType.LAZY)
	private Set<BookEntity> borrowedBooks;

	/**
	 * The associated user account.
	 */
	@OneToOne
	@JoinColumn(name = "user_id")
	private UserEntity user;
}
