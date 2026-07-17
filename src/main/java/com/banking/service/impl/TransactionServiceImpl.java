package com.banking.service.impl;

import com.banking.dto.TransactionDTO;
import com.banking.dto.TransferRequest;
import com.banking.entity.Account;
import com.banking.entity.Customer;
import com.banking.entity.Transaction;
import com.banking.exception.InvalidTransactionException;
import com.banking.exception.ResourceNotFoundException;
import com.banking.repository.AccountRepository;
import com.banking.repository.CustomerRepository;
import com.banking.repository.TransactionRepository;
import com.banking.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@AllArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private static final String INDIA = "INDIA";

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public TransactionDTO transfer(TransferRequest transferRequest) {
        Account fromAccount = accountRepository.findByAccountNumber(transferRequest.getFromAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Sender account not found"));

        Account toAccount;
        if (transferRequest.getReceiverPhoneNumber() != null && !transferRequest.getReceiverPhoneNumber().isBlank()) {
            Customer receiverCustomer = customerRepository.findByPhoneNumber(transferRequest.getReceiverPhoneNumber().trim())
                    .orElseThrow(() -> new ResourceNotFoundException("Receiver not found for provided phone number"));
            toAccount = receiverCustomer.getAccounts().stream()
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Receiver account not found"));
        } else {
            toAccount = accountRepository.findByAccountNumber(transferRequest.getToAccountNumber())
                    .orElseThrow(() -> new ResourceNotFoundException("Receiver account not found"));
        }

        if (fromAccount.getAccountNumber().equals(toAccount.getAccountNumber())) {
            throw new InvalidTransactionException("Sender and receiver accounts must be different");
        }

        BigDecimal amount = transferRequest.getAmount();
        if (amount == null || amount.compareTo(BigDecimal.ONE) < 0) {
            throw new InvalidTransactionException("Amount must be at least 1");
        }

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new InvalidTransactionException("Insufficient balance for this transfer");
        }

        validateRegion(fromAccount.getCustomer(), toAccount.getCustomer());

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        Transaction transaction = new Transaction();
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transaction.setAmount(amount);
        transaction.setTransactionType(Transaction.TransactionType.TRANSFER);
        transaction.setStatus(Transaction.TransactionStatus.SUCCESS);
        transaction.setDescription(transferRequest.getDescription());

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
        Transaction savedTransaction = transactionRepository.save(transaction);

        return mapToDTO(savedTransaction);
    }

    @Override
    public List<TransactionDTO> getTransactions(Long customerId) {
        List<Transaction> transactions = customerId == null
                ? transactionRepository.findAll()
                : transactionRepository.findLatestByCustomerId(customerId);

        return transactions.stream()
                .limit(10)
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public TransactionDTO getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
        return mapToDTO(transaction);
    }

    private void validateRegion(Customer sender, Customer receiver) {
        String senderCountry = normalizeCountry(sender.getCountry());
        String receiverCountry = normalizeCountry(receiver.getCountry());

        if (INDIA.equals(senderCountry) && !INDIA.equals(receiverCountry)) {
            throw new InvalidTransactionException("India based customers can transfer only to India based customers");
        }
    }

    private String normalizeCountry(String country) {
        return country == null || country.isBlank() ? INDIA : country.trim().toUpperCase();
    }

    private TransactionDTO mapToDTO(Transaction transaction) {
        Customer sender = transaction.getFromAccount().getCustomer();
        Customer receiver = transaction.getToAccount().getCustomer();

        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getId());
        dto.setFromAccountNumber(transaction.getFromAccount().getAccountNumber());
        dto.setToAccountNumber(transaction.getToAccount().getAccountNumber());
        dto.setSenderName(sender.getFirstName() + " " + sender.getLastName());
        dto.setReceiverName(receiver.getFirstName() + " " + receiver.getLastName());
        dto.setSenderCountry(normalizeCountry(sender.getCountry()));
        dto.setReceiverCountry(normalizeCountry(receiver.getCountry()));
        dto.setAmount(transaction.getAmount());
        dto.setTransactionType(transaction.getTransactionType().name());
        dto.setStatus(transaction.getStatus().name());
        dto.setDescription(transaction.getDescription());
        dto.setCreatedAt(transaction.getCreatedAt());
        return dto;
    }
}
