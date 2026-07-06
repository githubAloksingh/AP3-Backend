package com.banking.controller;

import com.banking.dto.TransactionDTO;
import com.banking.service.TransactionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing transactions.
 * Provides endpoints for deposits, withdrawals, and transaction history.
 */
@RestController
@RequestMapping("/api/transactions")
@AllArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * POST /api/transactions/deposit
     * Deposits money into an account.
     *
     * @param transactionDTO the deposit details
     * @return the transaction with HTTP 201 (Created)
     */
    @PostMapping("/deposit")
    public ResponseEntity<TransactionDTO> deposit(@Valid @RequestBody TransactionDTO transactionDTO) {
        TransactionDTO transaction = transactionService.deposit(transactionDTO);
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }

    /**
     * POST /api/transactions/withdraw
     * Withdraws money from an account.
     *
     * @param transactionDTO the withdrawal details
     * @return the transaction with HTTP 201 (Created)
     */
    @PostMapping("/withdraw")
    public ResponseEntity<TransactionDTO> withdraw(@Valid @RequestBody TransactionDTO transactionDTO) {
        TransactionDTO transaction = transactionService.withdraw(transactionDTO);
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }

    /**
     * GET /api/transactions/account/{accountId}
     * Retrieves the transaction history for a specific account.
     *
     * @param accountId the account ID
     * @return list of transactions with HTTP 200 (OK)
     */
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionDTO>> getTransactionHistory(@PathVariable Long accountId) {
        List<TransactionDTO> transactions = transactionService.getTransactionHistory(accountId);
        return ResponseEntity.ok(transactions);
    }
}