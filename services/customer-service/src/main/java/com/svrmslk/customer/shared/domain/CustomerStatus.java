// FILE: shared/domain/CustomerStatus.java
package com.svrmslk.customer.shared.domain;

public enum CustomerStatus {
    ACTIVE,
    SUSPENDED;

    public boolean isActive() {
        return this == ACTIVE;
    }

    public boolean canBook() {
        return this == ACTIVE;
    }
}