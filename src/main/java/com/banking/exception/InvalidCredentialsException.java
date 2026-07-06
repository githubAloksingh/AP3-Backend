package com.banking.exception;

/**
 * Exception thrown when login credentials (email or password) are invalid.
 * This is used in the authentication service when login fails.
 */
public class InvalidCredentialsException extends RuntimeException {

    /**
     * Constructs a new InvalidCredentialsException with the specified detail message.
     */
    public InvalidCredentialsException(String message) {
        super(message);
    }
}