package com.svrmslk.authservice.authentication.domain.exception;

public class InvalidCredentialsException extends DomainException {

    public InvalidCredentialsException(String message) {
        super(message);
    }
}