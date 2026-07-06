package com.banking.exception;

/**
 * Exception thrown when trying to create a resource that already exists.
 * For example, when a customer tries to register with an email that is already in use.
 */
public class DuplicateResourceException extends RuntimeException {

    /**
     * Constructs a new DuplicateResourceException with the specified detail message.
     */
    public DuplicateResourceException(String message) {
        super(message);
    }
}