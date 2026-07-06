package com.banking.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for transferring transaction data between layers.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {

    private Long id;

    @NotNull(message = "Transaction type is required")
    private String transactionType; // DEPOSIT or WITHDRAW

    @Positive(message = "Amount must be greater than zero")
    private BigDecimal amount;

    private LocalDateTime transactionDate;

    @NotNull(message = "Account ID is required")
    private Long accountId;

    private String accountNumber; // For response purposes only
}