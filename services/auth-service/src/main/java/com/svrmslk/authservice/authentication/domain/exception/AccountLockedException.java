package com.svrmslk.authservice.authentication.domain.exception;

public class AccountLockedException extends DomainException {

    public AccountLockedException(String message) {
        super(message);
    }
}