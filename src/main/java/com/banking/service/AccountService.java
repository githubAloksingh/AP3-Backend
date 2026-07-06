package com.banking.service;

import com.banking.dto.AccountDTO;

import java.util.List;

/**
 * Service interface for Account operations.
 * Defines the contract for account-related business logic.
 */
public interface AccountService {

    /**
     * Create a new account for a customer.
     */
    AccountDTO createAccount(AccountDTO accountDTO);

    /**
     * Get an account by its ID.
     */
    AccountDTO getAccountById(Long id);

    /**
     * Get all accounts.
     */
    List<AccountDTO> getAllAccounts();

    /**
     * Get all accounts belonging to a specific customer.
     */
    List<AccountDTO> getAccountsByCustomerId(Long customerId);

    /**
     * Update an existing account.
     */
    AccountDTO updateAccount(Long id, AccountDTO accountDTO);

    /**
     * Delete an account by its ID.
     */
    void deleteAccount(Long id);
}