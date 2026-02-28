package com.svrmslk.authservice.authentication.domain.policy;

/**
 * Policy for detecting and handling refresh token reuse.
 * Token reuse indicates potential security compromise.
 */
public class RefreshTokenReusePolicy {

    public boolean isTokenReuseDetected(boolean tokenExists, boolean tokenAlreadyUsed) {
        return !tokenExists || tokenAlreadyUsed;
    }

    public SecurityAction determineAction(boolean tokenReuseDetected) {
        if (tokenReuseDetected) {
            return SecurityAction.REVOKE_ALL_SESSIONS;
        }
        return SecurityAction.PROCEED;
    }

    public enum SecurityAction {
        PROCEED,
        REVOKE_ALL_SESSIONS
    }
}