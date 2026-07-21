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
import com.banking.security.AuthTokenService.AuthenticatedUser;

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
    public AccountDTO createAccount(AccountDTO accountDTO, AuthenticatedUser user) {

        // Find the customer
        Customer customer = customerRepository.findById(accountDTO.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + accountDTO.getCustomerId()));

        // Verify ownership: Admin or the owner customer
        if (!"ROLE_ADMIN".equals(user.role()) && !customer.getId().equals(user.customerId())) {
            throw new RuntimeException("Access denied: You cannot create an account for another customer");
        }

        // Create a new account
        Account account = new Account();
        account.setAccountNumber(generateAccountNumber());
        account.setAccountType(Account.AccountType.valueOf(accountDTO.getAccountType().toUpperCase()));
        account.setBalance(accountDTO.getBalance() != null ? accountDTO.getBalance() : BigDecimal.ZERO);
        account.setCustomer(customer);
        account.setCustomerName(customer.getFirstName() + " " + customer.getLastName());
        account.setCreatedAt(LocalDateTime.now());

        // Save the account
        Account savedAccount = accountRepository.save(account);

        return mapToDTO(savedAccount);
    }

    @Override
    public AccountDTO getAccountById(Long id, AuthenticatedUser user) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));

        // Verify ownership: Admin or the owner customer
        if (!"ROLE_ADMIN".equals(user.role()) && !account.getCustomer().getId().equals(user.customerId())) {
            throw new ResourceNotFoundException("Account not found with id: " + id);
        }

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

    @Override
    @Transactional
    public void deleteAccount(Long id, AuthenticatedUser user) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));

        if (!"ROLE_ADMIN".equals(user.role())) {
            throw new RuntimeException("Direct deletion is not permitted for customers. Please request account deletion for admin approval.");
        }

        accountRepository.delete(account);
    }

    @Override
    @Transactional
    public AccountDTO requestAccountDeletion(Long id, AuthenticatedUser user) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));

        if (!"ROLE_ADMIN".equals(user.role()) && !account.getCustomer().getId().equals(user.customerId())) {
            throw new ResourceNotFoundException("Account not found with id: " + id);
        }

        account.setDeletionRequested(true);
        Account saved = accountRepository.save(account);
        return mapToDTO(saved);
    }

    @Override
    @Transactional
    public AccountDTO approveAccountDeletion(Long id, AuthenticatedUser user) {
        if (!"ROLE_ADMIN".equals(user.role())) {
            throw new RuntimeException("Access denied: Only admins can approve account deletion.");
        }

        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));

        AccountDTO dto = mapToDTO(account);
        accountRepository.delete(account);
        return dto;
    }

    @Override
    @Transactional
    public AccountDTO rejectAccountDeletion(Long id, AuthenticatedUser user) {
        if (!"ROLE_ADMIN".equals(user.role())) {
            throw new RuntimeException("Access denied: Only admins can reject account deletion.");
        }

        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));

        account.setDeletionRequested(false);
        Account saved = accountRepository.save(account);
        return mapToDTO(saved);
    }

    @Override
    @Transactional
    public AccountDTO switchAccountType(Long id, AuthenticatedUser user) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));

        if (!"ROLE_ADMIN".equals(user.role()) && !account.getCustomer().getId().equals(user.customerId())) {
            throw new ResourceNotFoundException("Account not found with id: " + id);
        }

        if (account.getAccountType() == Account.AccountType.SAVINGS) {
            account.setAccountType(Account.AccountType.CURRENT);
        } else {
            account.setAccountType(Account.AccountType.SAVINGS);
        }

        Account saved = accountRepository.save(account);
        return mapToDTO(saved);
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
        dto.setDeletionRequested(account.isDeletionRequested());
        return dto;
    }
}
