package com.example.library.repository;

import com.example.library.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository interface for UserEntity.
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

	/**
	 * Finds a user by username.
	 *
	 * @param username Username of the user
	 * @return Optional containing UserEntity if found
	 */
	Optional<UserEntity> findByUsername(String username);

	/**
	 * Checks if a user exists with the given username.
	 *
	 * @param username Username to check
	 * @return true if such a user exists, false otherwise
	 */
	Boolean existsByUsername(String username);
}
