package com.svrmslk.authservice.authentication.application.port.out;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Port for managing email verification tokens.
 */
public interface EmailVerificationTokenRepositoryPort {

    EmailVerificationToken save(EmailVerificationToken token);

    Optional<EmailVerificationToken> findByTokenHash(String tokenHash);

    void deleteByUserId(UUID userId);

    void deleteExpiredTokens(Instant before);

    record EmailVerificationToken(
            UUID id,
            UUID userId,
            String tokenHash,
            Instant expiresAt,
            Instant createdAt,
            Instant verifiedAt,
            boolean used
    ) {}
}