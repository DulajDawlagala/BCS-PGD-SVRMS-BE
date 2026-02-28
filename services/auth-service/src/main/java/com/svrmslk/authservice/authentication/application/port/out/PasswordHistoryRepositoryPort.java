package com.svrmslk.authservice.authentication.application.port.out;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Port for managing password history to prevent password reuse.
 */
public interface PasswordHistoryRepositoryPort {

    void save(PasswordHistory history);

    List<PasswordHistory> findRecentByUserId(UUID userId, int limit);

    void deleteByUserId(UUID userId);

    record PasswordHistory(
            UUID id,
            UUID userId,
            String passwordHash,
            Instant createdAt
    ) {}
}