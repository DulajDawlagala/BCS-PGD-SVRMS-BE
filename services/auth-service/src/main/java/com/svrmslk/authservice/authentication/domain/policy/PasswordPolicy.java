package com.svrmslk.authservice.authentication.domain.policy;

import com.svrmslk.authservice.authentication.domain.exception.WeakPasswordException;

import java.util.regex.Pattern;

public class PasswordPolicy {

    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 128;
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#$%^&*(),.?\":{}|<>]");

    public void validate(String password) {
        if (password.length() < MIN_LENGTH) {
            throw new WeakPasswordException("Password must be at least " + MIN_LENGTH + " characters");
        }

        if (password.length() > MAX_LENGTH) {
            throw new WeakPasswordException("Password must not exceed " + MAX_LENGTH + " characters");
        }

        if (!UPPERCASE_PATTERN.matcher(password).find()) {
            throw new WeakPasswordException("Password must contain at least one uppercase letter");
        }

        if (!LOWERCASE_PATTERN.matcher(password).find()) {
            throw new WeakPasswordException("Password must contain at least one lowercase letter");
        }

        if (!DIGIT_PATTERN.matcher(password).find()) {
            throw new WeakPasswordException("Password must contain at least one digit");
        }

        if (!SPECIAL_CHAR_PATTERN.matcher(password).find()) {
            throw new WeakPasswordException("Password must contain at least one special character");
        }
    }
}