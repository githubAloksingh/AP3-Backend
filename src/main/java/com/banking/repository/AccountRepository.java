package com.banking.repository;

import com.banking.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Account entity.
 * Provides CRUD operations on the accounts table.
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * Find all accounts belonging to a specific customer.
     */
    List<Account> findByCustomerId(Long customerId);

    /**
     * Find an account by its account number.
     */
    Optional<Account> findByAccountNumber(String accountNumber);

    /**
     * Check if an account with the given account number already exists.
     */
    boolean existsByAccountNumber(String accountNumber);
}