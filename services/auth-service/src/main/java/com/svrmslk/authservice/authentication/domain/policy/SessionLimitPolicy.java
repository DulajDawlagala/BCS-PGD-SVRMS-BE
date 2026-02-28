package com.svrmslk.authservice.authentication.domain.policy;

/**
 * Policy for limiting concurrent user sessions.
 */
public class SessionLimitPolicy {

    private static final int MAX_CONCURRENT_SESSIONS = 5;

    public int getMaxConcurrentSessions() {
        return MAX_CONCURRENT_SESSIONS;
    }

    public boolean exceedsSessionLimit(long currentSessions) {
        return currentSessions >= MAX_CONCURRENT_SESSIONS;
    }
}