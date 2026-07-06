package com.banking.repository;

import com.banking.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Customer entity.
 * Provides CRUD operations on the customers table.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * Find a customer by their email address.
     */
    Optional<Customer> findByEmail(String email);

    /**
     * Find a customer by their phone number.
     */
    Optional<Customer> findByPhoneNumber(String phoneNumber);

    /**
     * Check if a customer with the given email already exists.
     */
    boolean existsByEmail(String email);

    /**
     * Check if a customer with the given phone number already exists.
     */
    boolean existsByPhoneNumber(String phoneNumber);
}