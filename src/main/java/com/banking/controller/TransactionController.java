package com.banking.controller;

import com.banking.dto.TransactionDTO;
import com.banking.dto.TransferRequest;
import com.banking.service.TransactionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@AllArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/transfer")
    public ResponseEntity<TransactionDTO> transfer(@Valid @RequestBody TransferRequest transferRequest) {
        TransactionDTO transaction = transactionService.transfer(transferRequest);
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TransactionDTO>> getTransactions(@RequestParam(required = false) Long customerId) {
        return ResponseEntity.ok(transactionService.getTransactions(customerId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> getTransactionById(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }
}
