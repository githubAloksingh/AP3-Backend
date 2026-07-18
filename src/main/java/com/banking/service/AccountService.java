package com.banking.service;

import com.banking.dto.AccountDTO;

import java.util.List;

import com.banking.security.AuthTokenService.AuthenticatedUser;

/**
 * Service interface for Account operations.
 * Defines the contract for account-related business logic.
 */
public interface AccountService {

    /**
     * Create a new account for a customer.
     */
    AccountDTO createAccount(AccountDTO accountDTO, AuthenticatedUser user);

    /**
     * Get an account by its ID.
     */
    AccountDTO getAccountById(Long id, AuthenticatedUser user);

    /**
     * Get all accounts.
     */
    List<AccountDTO> getAllAccounts();

    /**
     * Get all accounts belonging to a specific customer.
     */
    List<AccountDTO> getAccountsByCustomerId(Long customerId);

    /**
     * Close (delete) an account by its ID.
     */
    void deleteAccount(Long id, AuthenticatedUser user);
}
