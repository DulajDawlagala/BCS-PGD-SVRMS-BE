// FILE: shared/exception/CustomerAlreadyExistsException.java
package com.svrmslk.customer.shared.exception;

public class CustomerAlreadyExistsException extends DomainException {

    public CustomerAlreadyExistsException(String email) {
        super("Customer already exists with email: " + email);
    }
}