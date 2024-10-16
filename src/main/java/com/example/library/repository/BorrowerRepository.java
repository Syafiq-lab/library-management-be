package com.example.library.repository;

import com.example.library.model.BorrowerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository interface for BorrowerEntity.
 */
@Repository
public interface BorrowerRepository extends JpaRepository<BorrowerEntity, String> {

	/**
	 * Finds a borrower by associated username.
	 *
	 * @param username Username of the user
	 * @return Optional containing BorrowerEntity if found
	 */
	Optional<BorrowerEntity> findByUserUsername(String username);

	/**
	 * Checks if a borrower exists with the given email.
	 *
	 * @param email Email address
	 * @return true if such a borrower exists, false otherwise
	 */
	boolean existsByEmail(String email);
}
