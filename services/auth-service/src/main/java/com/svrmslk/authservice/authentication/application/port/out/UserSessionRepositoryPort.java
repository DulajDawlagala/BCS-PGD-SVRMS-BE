package com.svrmslk.authservice.authentication.application.port.out;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port for managing user sessions.
 */
public interface UserSessionRepositoryPort {

    UserSession save(UserSession session);

    Optional<UserSession> findBySessionToken(String sessionToken);

    List<UserSession> findActiveByUserId(UUID userId);

    long countActiveByUserId(UUID userId);

    void terminateAllByUserId(UUID userId);

    void deleteExpiredSessions(Instant before);

    record UserSession(
            UUID id,
            UUID userId,
            String sessionToken,
            String deviceInfo,
            String ipAddress,
            String userAgent,
            boolean active,
            Instant createdAt,
            Instant lastAccessedAt,
            Instant expiresAt,
            Instant terminatedAt
    ) {}
}