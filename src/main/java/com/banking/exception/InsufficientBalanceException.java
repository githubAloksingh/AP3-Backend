package com.banking.exception;

/**
 * Exception thrown when a withdrawal amount exceeds the available balance
 * in the account.
 */
public class InsufficientBalanceException extends RuntimeException {

    /**
     * Constructs a new InsufficientBalanceException with the specified detail message.
     */
    public InsufficientBalanceException(String message) {
        super(message);
    }
}