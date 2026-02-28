// FILE: shared/exception/ValidationException.java
package com.svrmslk.customer.shared.exception;

import java.util.Map;

public class ValidationException extends DomainException {

    private final Map<String, String> errors;

    public ValidationException(String message) {
        super(message);
        this.errors = Map.of();
    }

    public ValidationException(Map<String, String> errors) {
        super("Validation failed");
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}