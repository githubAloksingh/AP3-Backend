package com.banking.exception;

/**
 * Exception thrown when a requested resource is not found in the database.
 * This is used in service layer methods when an entity cannot be found by its ID.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a new ResourceNotFoundException with the specified detail message.
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}