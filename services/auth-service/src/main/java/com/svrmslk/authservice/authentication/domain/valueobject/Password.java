package com.svrmslk.authservice.authentication.domain.valueobject;

import com.svrmslk.authservice.authentication.domain.exception.WeakPasswordException;
import com.svrmslk.authservice.authentication.domain.policy.PasswordPolicy;

import java.util.Objects;

public class Password {

    private final String value;

    private Password(String value) {
        this.value = value;
    }

    public static Password of(String rawPassword, PasswordPolicy policy) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new WeakPasswordException("Password cannot be empty");
        }

        policy.validate(rawPassword);

        return new Password(rawPassword);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Password password = (Password) o;
        return Objects.equals(value, password.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}