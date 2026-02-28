// FILE: shared/exception/CustomerNotFoundException.java
package com.svrmslk.customer.shared.exception;

public class CustomerNotFoundException extends DomainException {

    public CustomerNotFoundException(String customerId) {
        super("Customer not found: " + customerId);
    }
}
