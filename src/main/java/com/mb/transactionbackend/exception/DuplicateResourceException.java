package com.mb.transactionbackend.exception;

/** Thrown when a unique constraint is violated (e.g., username already exists). */
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}