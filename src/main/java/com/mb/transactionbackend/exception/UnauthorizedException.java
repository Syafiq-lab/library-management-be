package com.mb.transactionbackend.exception;

/** Thrown when an operation requires authentication but none/invalid is present. */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}