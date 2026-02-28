// FILE: shared/domain/CustomerId.java
package com.svrmslk.customer.shared.domain;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class CustomerId implements Serializable {

    private String value;

    protected CustomerId() {} // JPA only

    private CustomerId(String value) {
        this.value = Objects.requireNonNull(value, "CustomerId cannot be null");
    }

    public static CustomerId generate() {
        return new CustomerId(UUID.randomUUID().toString());
    }

    public static CustomerId of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("CustomerId cannot be blank");
        }
        return new CustomerId(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomerId)) return false;
        CustomerId that = (CustomerId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
