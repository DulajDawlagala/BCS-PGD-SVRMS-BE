package com.svrmslk.authservice.authentication.application.port.out;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepositoryPort {

    void save(RefreshToken refreshToken);

    Optional<RefreshToken> findByToken(String token);

    void deleteByToken(String token);

    void deleteByUserId(UUID userId);

    record RefreshToken(
            UUID id,
            UUID userId,
            String token,
            Instant expiresAt,
            Instant createdAt
    ) {}
}