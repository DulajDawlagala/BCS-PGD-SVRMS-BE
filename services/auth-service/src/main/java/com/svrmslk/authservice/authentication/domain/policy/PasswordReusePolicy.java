package com.svrmslk.authservice.authentication.domain.policy;

/**
 * Policy for preventing password reuse.
 */
public class PasswordReusePolicy {

    private static final int PASSWORD_HISTORY_LIMIT = 5;

    public int getPasswordHistoryLimit() {
        return PASSWORD_HISTORY_LIMIT;
    }

    public boolean isPasswordReused(String newPasswordHash, java.util.List<String> recentHashes) {
        return recentHashes.stream()
                .anyMatch(hash -> java.security.MessageDigest.isEqual(
                        hash.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                        newPasswordHash.getBytes(java.nio.charset.StandardCharsets.UTF_8)
                ));
    }
}