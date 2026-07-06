package com.banking.service.impl;

import com.banking.dto.TransactionDTO;
import com.banking.entity.Account;
import com.banking.entity.Transaction;
import com.banking.exception.InsufficientBalanceException;
import com.banking.exception.ResourceNotFoundException;
import com.banking.repository.AccountRepository;
import com.banking.repository.TransactionRepository;
import com.banking.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of TransactionService interface.
 * Contains the business logic for deposits, withdrawals, and transaction history.
 */
@Service
@AllArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public TransactionDTO deposit(TransactionDTO transactionDTO) {

        // Find the account
        Account account = accountRepository.findById(transactionDTO.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + transactionDTO.getAccountId()));

        // Create a deposit transaction
        Transaction transaction = new Transaction();
        transaction.setTransactionType(Transaction.TransactionType.DEPOSIT);
        transaction.setAmount(transactionDTO.getAmount());
        transaction.setAccount(account);

        // Update account balance
        account.setBalance(account.getBalance().add(transactionDTO.getAmount()));
        accountRepository.save(account);

        // Save the transaction
        Transaction savedTransaction = transactionRepository.save(transaction);

        return mapToDTO(savedTransaction);
    }

    @Override
    @Transactional
    public TransactionDTO withdraw(TransactionDTO transactionDTO) {

        // Find the account
        Account account = accountRepository.findById(transactionDTO.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + transactionDTO.getAccountId()));

        // Check if the account has sufficient balance
        if (account.getBalance().compareTo(transactionDTO.getAmount()) < 0) {
            throw new InsufficientBalanceException(
                    "Insufficient balance in account " + account.getAccountNumber() +
                    ". Current balance: " + account.getBalance() +
                    ", Requested withdrawal: " + transactionDTO.getAmount()
            );
        }

        // Create a withdrawal transaction
        Transaction transaction = new Transaction();
        transaction.setTransactionType(Transaction.TransactionType.WITHDRAW);
        transaction.setAmount(transactionDTO.getAmount());
        transaction.setAccount(account);

        // Update account balance (subtract the withdrawal amount)
        account.setBalance(account.getBalance().subtract(transactionDTO.getAmount()));
        accountRepository.save(account);

        // Save the transaction
        Transaction savedTransaction = transactionRepository.save(transaction);

        return mapToDTO(savedTransaction);
    }

    @Override
    public List<TransactionDTO> getTransactionHistory(Long accountId) {

        // Verify the account exists
        if (!accountRepository.existsById(accountId)) {
            throw new ResourceNotFoundException("Account not found with id: " + accountId);
        }

        // Get all transactions for the account, ordered by date descending
        List<Transaction> transactions = transactionRepository.findByAccountIdOrderByTransactionDateDesc(accountId);

        return transactions.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Converts a Transaction entity to a TransactionDTO.
     */
    private TransactionDTO mapToDTO(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getId());
        dto.setTransactionType(transaction.getTransactionType().name());
        dto.setAmount(transaction.getAmount());
        dto.setTransactionDate(transaction.getTransactionDate());
        dto.setAccountId(transaction.getAccount().getId());
        dto.setAccountNumber(transaction.getAccount().getAccountNumber());
        return dto;
    }
}