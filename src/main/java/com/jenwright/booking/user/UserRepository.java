package com.jenwright.booking.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for managing User entities.
 *
 * Provides data access operations for users including retrieval and existence
 * checking. This interface extends JpaRepository to inherit standard CRUD operations
 * and adds custom query methods for user-specific lookups.
 *
 * @author jen
 * @version 1.0
 */
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Finds a user by their email address.
     *
     * @param email the email address to search for
     * @return an Optional containing the User if found, or an empty Optional if not found
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks whether a user with the specified email already exists.
     *
     * @param email the email address to check
     * @return true if a user with the email exists, false otherwise
     */
    boolean existsByEmail(String email);
}
