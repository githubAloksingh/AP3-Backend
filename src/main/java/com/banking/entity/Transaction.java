package com.banking.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a financial transaction (DEPOSIT or WITHDRAW)
 * performed on a bank account.
 */
@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Transaction type can be DEPOSIT or WITHDRAW.
     * Stored as a String in the database.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Positive(message = "Amount must be greater than zero")
    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "transaction_date", nullable = false, updatable = false)
    private LocalDateTime transactionDate;

    /**
     * The account on which this transaction was performed.
     * JsonIgnore prevents infinite recursion when serializing JSON.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    @JsonIgnore
    private Account account;

    /**
     * Sets the transaction timestamp before the entity is first persisted.
     */
    @PrePersist
    protected void onCreate() {
        this.transactionDate = LocalDateTime.now();
    }

    /**
     * Enum representing the types of transactions.
     */
    public enum TransactionType {
        DEPOSIT,
        WITHDRAW
    }
}