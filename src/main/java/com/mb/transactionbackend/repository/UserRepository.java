package com.mb.transactionbackend.repository;

import com.mb.transactionbackend.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsernameAndDeletedFalse(String username);
    
    boolean existsByUsernameAndDeletedFalse(String username);
    
    @Query("SELECT u FROM User u WHERE u.username = :username AND u.deleted = false")
    Optional<User> findActiveUser(String username);

    User findByUsername(String username);

    boolean existsByUsername(String username);

    Optional<User> findOptionalByUsername(String username);

    boolean existsByUsernameIgnoreCase(@NotBlank(message = "Username cannot be empty") @Size(min = 4, max = 50, message = "Username must be between 4 and 50 characters") String username);

    Optional<Object> findByUsernameIgnoreCase(String username);
}