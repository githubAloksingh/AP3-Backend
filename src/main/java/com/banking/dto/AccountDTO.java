package com.banking.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO for transferring account data between layers.
 * Used for both request and response to avoid exposing the entity directly.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {

    private Long id;

    private String accountNumber;

    @NotNull(message = "Account type is required")
    private String accountType; // SAVINGS or CURRENT

    @PositiveOrZero(message = "Balance cannot be negative")
    private BigDecimal balance;

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    private String customerName; // For response purposes only

    private boolean deletionRequested;
}