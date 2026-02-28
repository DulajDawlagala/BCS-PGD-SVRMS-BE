package com.svrmslk.authservice.authentication.domain.exception;

public class WeakPasswordException extends DomainException {

    public WeakPasswordException(String message) {
        super(message);
    }
}