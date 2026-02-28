package com.svrmslk.authservice.authentication.application.port.out;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Port for managing OTP tokens.
 */
public interface OtpTokenRepositoryPort {

    OtpToken save(OtpToken token);

    Optional<OtpToken> findActiveByUserIdAndType(UUID userId, String otpType);

    void deleteByUserId(UUID userId);

    void deleteExpiredTokens(Instant before);

    record OtpToken(
            UUID id,
            UUID userId,
            String otpHash,
            String otpType,
            Instant expiresAt,
            Instant createdAt,
            Instant verifiedAt,
            int failedAttempts,
            boolean used
    ) {}
}