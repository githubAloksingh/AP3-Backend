package com.banking.service;

import com.banking.dto.TransactionDTO;

import java.util.List;

/**
 * Service interface for Transaction operations.
 * Defines the contract for deposit, withdraw, and transaction history.
 */
public interface TransactionService {

    /**
     * Deposit money into an account.
     * The account balance will be updated after a successful deposit.
     */
    TransactionDTO deposit(TransactionDTO transactionDTO);

    /**
     * Withdraw money from an account.
     * The withdrawal amount cannot exceed the current balance.
     * The account balance will be updated after a successful withdrawal.
     */
    TransactionDTO withdraw(TransactionDTO transactionDTO);

    /**
     * Get the transaction history for a specific account.
     */
    List<TransactionDTO> getTransactionHistory(Long accountId);
}