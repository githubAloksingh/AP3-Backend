package com.banking.service;

import com.banking.dto.TransactionDTO;
import com.banking.dto.TransferRequest;

import java.util.List;

public interface TransactionService {

    TransactionDTO transfer(TransferRequest transferRequest);

    List<TransactionDTO> getTransactions(Long customerId);

    TransactionDTO getTransactionById(Long id);
}
