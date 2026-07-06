package com.banking.repository;

import com.banking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entity.
 * Provides CRUD operations on the users table.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find a user by their email address.
     * Used during login and signup to check existing users.
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if a user with the given email already exists.
     * Used during signup to prevent duplicate registrations.
     */
    boolean existsByEmail(String email);
}