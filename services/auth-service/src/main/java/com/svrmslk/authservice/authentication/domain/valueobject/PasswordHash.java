package com.svrmslk.authservice.authentication.domain.valueobject;

import java.util.Objects;

public class PasswordHash {

    private final String value;

    private PasswordHash(String value) {
        this.value = value;
    }

    public static PasswordHash of(String encodedHash) {
        if (encodedHash == null || encodedHash.isBlank()) {
            throw new IllegalArgumentException("Password hash cannot be empty");
        }
        return new PasswordHash(encodedHash);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PasswordHash that = (PasswordHash) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}