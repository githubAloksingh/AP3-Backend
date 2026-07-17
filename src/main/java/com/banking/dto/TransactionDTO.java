package com.banking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {

    private Long id;
    private String fromAccountNumber;
    private String toAccountNumber;
    private String senderName;
    private String receiverName;
    private String senderCountry;
    private String receiverCountry;
    private BigDecimal amount;
    private String transactionType;
    private String status;
    private String description;
    private LocalDateTime createdAt;
}
