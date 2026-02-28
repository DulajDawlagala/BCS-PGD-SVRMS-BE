package com.svrmslk.booking.shared.exception;

public class ValidationException extends DomainException {
    public ValidationException(String message) {
        super(message);
    }
}