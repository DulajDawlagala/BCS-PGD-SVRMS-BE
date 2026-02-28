// FILE: shared/domain/Email.java
package com.svrmslk.customer.shared.domain;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

@Embeddable
public class Email implements Serializable {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private String value;

    protected Email() {} // JPA only

    private Email(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Email cannot be blank");
        }
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid email format: " + value);
        }
        this.value = value.toLowerCase().trim();
    }

    public static Email of(String value) {
        return new Email(value);
    }

    public String getValue() {
        return value;
    }

    public String getDomain() {
        return value.substring(value.indexOf('@') + 1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email)) return false;
        Email email = (Email) o;
        return Objects.equals(value, email.value);
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
