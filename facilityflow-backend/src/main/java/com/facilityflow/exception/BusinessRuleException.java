package com.facilityflow.exception;

/**
 * Raised when an operation would violate a domain/business rule
 * (e.g. double-booking a room, closing an already-closed ticket).
 */
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}
