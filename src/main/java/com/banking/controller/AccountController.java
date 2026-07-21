package com.banking.controller;

import com.banking.dto.AccountDTO;
import com.banking.service.AccountService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.banking.security.CurrentUser;
import com.banking.security.AuthTokenService.AuthenticatedUser;

import java.util.List;

/**
 * REST controller for managing bank accounts.
 * Provides CRUD endpoints for the Account entity.
 */
@RestController
@RequestMapping("/api/accounts")
@AllArgsConstructor
public class AccountController {

    private final AccountService accountService;

    /**
     * POST /api/accounts
     * Creates a new account for a customer.
     *
     * @param accountDTO the account data
     * @return the created account with HTTP 201 (Created)
     */
    @PostMapping
    public ResponseEntity<AccountDTO> createAccount(
            @Valid @RequestBody AccountDTO accountDTO,
            @CurrentUser AuthenticatedUser user) {
        AccountDTO createdAccount = accountService.createAccount(accountDTO, user);
        return new ResponseEntity<>(createdAccount, HttpStatus.CREATED);
    }

    /**
     * GET /api/accounts
     * Retrieves all accounts.
     * Optionally filter by customerId query parameter.
     *
     * @param customerId optional customer ID to filter accounts
     * @return list of accounts with HTTP 200 (OK)
     */
    @GetMapping
    public ResponseEntity<List<AccountDTO>> getAllAccounts(
            @RequestParam(required = false) Long customerId) {
        List<AccountDTO> accounts;
        if (customerId != null) {
            accounts = accountService.getAccountsByCustomerId(customerId);
        } else {
            accounts = accountService.getAllAccounts();
        }
        return ResponseEntity.ok(accounts);
    }

    /**
     * GET /api/accounts/{id}
     * Retrieves an account by its ID.
     *
     * @param id the account ID
     * @return the account with HTTP 200 (OK)
     */
    @GetMapping("/{id}")
    public ResponseEntity<AccountDTO> getAccountById(
            @PathVariable Long id,
            @CurrentUser AuthenticatedUser user) {
        AccountDTO account = accountService.getAccountById(id, user);
        return ResponseEntity.ok(account);
    }

    /**
     * DELETE /api/accounts/{id}
     * Closes/deletes an account by its ID (Admin only).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(
            @PathVariable Long id,
            @CurrentUser AuthenticatedUser user) {
        accountService.deleteAccount(id, user);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /api/accounts/{id}/request-deletion
     * Submits an account deletion request for admin approval.
     */
    @PostMapping("/{id}/request-deletion")
    public ResponseEntity<AccountDTO> requestAccountDeletion(
            @PathVariable Long id,
            @CurrentUser AuthenticatedUser user) {
        AccountDTO account = accountService.requestAccountDeletion(id, user);
        return ResponseEntity.ok(account);
    }

    /**
     * POST /api/accounts/{id}/approve-deletion
     * Approves and executes account deletion (Admin only).
     */
    @PostMapping("/{id}/approve-deletion")
    public ResponseEntity<AccountDTO> approveAccountDeletion(
            @PathVariable Long id,
            @CurrentUser AuthenticatedUser user) {
        AccountDTO account = accountService.approveAccountDeletion(id, user);
        return ResponseEntity.ok(account);
    }

    /**
     * POST /api/accounts/{id}/reject-deletion
     * Rejects account deletion request (Admin only).
     */
    @PostMapping("/{id}/reject-deletion")
    public ResponseEntity<AccountDTO> rejectAccountDeletion(
            @PathVariable Long id,
            @CurrentUser AuthenticatedUser user) {
        AccountDTO account = accountService.rejectAccountDeletion(id, user);
        return ResponseEntity.ok(account);
    }

    /**
     * PUT /api/accounts/{id}/switch-type
     * Switches account type between SAVINGS and CURRENT.
     */
    @PutMapping("/{id}/switch-type")
    public ResponseEntity<AccountDTO> switchAccountType(
            @PathVariable Long id,
            @CurrentUser AuthenticatedUser user) {
        AccountDTO updatedAccount = accountService.switchAccountType(id, user);
        return ResponseEntity.ok(updatedAccount);
    }
}
