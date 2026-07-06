package com.banking.service.impl;

import com.banking.dto.AccountDTO;
import com.banking.entity.Account;
import com.banking.entity.Customer;
import com.banking.exception.ResourceNotFoundException;
import com.banking.repository.AccountRepository;
import com.banking.repository.CustomerRepository;
import com.banking.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Implementation of AccountService interface.
 * Contains the business logic for managing bank accounts.
 */
@Service
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public AccountDTO createAccount(AccountDTO accountDTO) {

        // Find the customer
        Customer customer = customerRepository.findById(accountDTO.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + accountDTO.getCustomerId()));

        // Create a new account
        Account account = new Account();
        account.setAccountNumber(generateAccountNumber());
        account.setAccountType(Account.AccountType.valueOf(accountDTO.getAccountType().toUpperCase()));
        account.setBalance(accountDTO.getBalance() != null ? accountDTO.getBalance() : BigDecimal.ZERO);
        account.setCustomer(customer);
        account.setCreatedAt(LocalDateTime.now());

        // Save the account
        Account savedAccount = accountRepository.save(account);

        return mapToDTO(savedAccount);
    }

    @Override
    public AccountDTO getAccountById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));
        return mapToDTO(account);
    }

    @Override
    public List<AccountDTO> getAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        return accounts.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AccountDTO> getAccountsByCustomerId(Long customerId) {
        List<Account> accounts = accountRepository.findByCustomerId(customerId);
        return accounts.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Generates a unique 10-digit account number.
     */
    private String generateAccountNumber() {
        Random random = new Random();
        StringBuilder accountNumber;
        do {
            accountNumber = new StringBuilder();
            for (int i = 0; i < 10; i++) {
                accountNumber.append(random.nextInt(10));
            }
        } while (accountRepository.existsByAccountNumber(accountNumber.toString()));

        return accountNumber.toString();
    }

    /**
     * Converts an Account entity to an AccountDTO.
     */
    private AccountDTO mapToDTO(Account account) {
        AccountDTO dto = new AccountDTO();
        dto.setId(account.getId());
        dto.setAccountNumber(account.getAccountNumber());
        dto.setAccountType(account.getAccountType().name());
        dto.setBalance(account.getBalance());
        dto.setCustomerId(account.getCustomer().getId());
        dto.setCustomerName(account.getCustomer().getFirstName() + " " + account.getCustomer().getLastName());
        return dto;
    }
}
