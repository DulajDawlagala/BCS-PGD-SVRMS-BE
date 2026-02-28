package com.svrmslk.authservice.authentication.application.port.out;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port for managing external OAuth2 identities linked to user accounts.
 */
public interface ExternalIdentityRepositoryPort {

    ExternalIdentity save(ExternalIdentity externalIdentity);

    Optional<ExternalIdentity> findByProviderAndProviderUserId(String provider, String providerUserId);

    List<ExternalIdentity> findByUserId(UUID userId);

    boolean existsByProviderAndProviderUserId(String provider, String providerUserId);

    void deleteById(UUID id);

    record ExternalIdentity(
            UUID id,
            UUID userId,
            String provider,
            String providerUserId,
            String providerEmail,
            String providerName,
            String accessTokenHash,
            String refreshTokenHash,
            Instant tokenExpiresAt,
            Instant linkedAt,
            Instant lastUsedAt
    ) {}
}