package com.svrmslk.authservice.authentication.application.port.out;

import java.util.Set;

/**
 * Port for verifying Google ID tokens.
 *
 * Contract:
 * - Implementations MUST fully validate token signature, expiration,
 *   issuer, and audience.
 * - On failure, an exception MUST be thrown.
 * - On success, returned token is GUARANTEED to be valid.
 */
public interface GoogleTokenVerifierPort {

    /**
     * Verifies a Google ID token.
     *
     * @param idToken the raw Google ID token
     * @return verified and trusted token claims
     * @throws InvalidOAuthTokenException if verification fails
     */
    VerifiedToken verify(String idToken);

    /**
     * Verified Google ID token claims.
     * All fields are trusted and extracted only after successful verification.
     */
    record VerifiedToken(
            String subject,           // Google user ID (sub claim)
            String email,
            boolean emailVerified,
            String name,
            String picture,
            String issuer,
            Set<String> audience      // Supports multi-client IDs
    ) {}
}
