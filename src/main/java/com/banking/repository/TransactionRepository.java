package com.banking.repository;

import com.banking.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Transaction entity.
 * Provides CRUD operations on the transactions table.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Find all transactions for a specific account, ordered by date descending.
     */
    List<Transaction> findByAccountIdOrderByTransactionDateDesc(Long accountId);
}