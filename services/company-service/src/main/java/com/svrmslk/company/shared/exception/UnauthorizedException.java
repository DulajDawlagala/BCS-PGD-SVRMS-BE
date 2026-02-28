package com.svrmslk.company.shared.exception;

public class UnauthorizedException extends DomainException {
    public UnauthorizedException(String message) {
        super(message);
    }
}