package com.svrmslk.authservice.authentication.domain.valueobject;

import com.svrmslk.authservice.authentication.domain.exception.InvalidEmailException;

import java.util.Objects;
import java.util.regex.Pattern;

public class Email {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private final String value;

    private Email(String value) {
        this.value = value;
    }

    public static Email of(String email) {
        if (email == null || email.isBlank()) {
            throw new InvalidEmailException("Email cannot be empty");
        }

        String normalized = email.trim().toLowerCase();

        if (!EMAIL_PATTERN.matcher(normalized).matches()) {
            throw new InvalidEmailException("Invalid email format: " + email);
        }

        if (normalized.length() > 255) {
            throw new InvalidEmailException("Email too long");
        }

        return new Email(normalized);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
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