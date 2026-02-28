package com.svrmslk.authservice.authentication.domain.policy;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class AccountLockoutPolicy {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCKOUT_DURATION_MINUTES = 15;

    public boolean shouldLockAccount(int failedAttempts) {
        return failedAttempts >= MAX_FAILED_ATTEMPTS;
    }

    public Instant calculateLockoutDuration(int failedAttempts) {
        long minutes = LOCKOUT_DURATION_MINUTES * (failedAttempts / MAX_FAILED_ATTEMPTS);
        return Instant.now().plus(minutes, ChronoUnit.MINUTES);
    }
}