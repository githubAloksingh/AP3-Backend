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

    /**
     * Submit a request to delete an account (user action).
     */
    AccountDTO requestAccountDeletion(Long id, AuthenticatedUser user);

    /**
     * Approve and execute account deletion (admin action).
     */
    AccountDTO approveAccountDeletion(Long id, AuthenticatedUser user);

    /**
     * Reject account deletion request (admin action).
     */
    AccountDTO rejectAccountDeletion(Long id, AuthenticatedUser user);

    /**
     * Switch account type between SAVINGS and CURRENT.
     */
    AccountDTO switchAccountType(Long id, AuthenticatedUser user);
}
