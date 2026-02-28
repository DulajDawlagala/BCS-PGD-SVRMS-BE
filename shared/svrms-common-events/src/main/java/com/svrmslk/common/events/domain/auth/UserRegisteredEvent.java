package com.svrmslk.common.events.domain.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.svrmslk.common.events.api.Event;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when a new user registers in the system.
 *
 * @author Platform Team
 * @since 1.0.0
 */
public record UserRegisteredEvent(
        @JsonProperty("eventId") String eventId,
        @JsonProperty("userId") String userId,
        @JsonProperty("email") String email,
        @JsonProperty("tenantId") String tenantId,
        @JsonProperty("occurredAt") Instant occurredAt,
        @JsonProperty("registrationSource") String registrationSource
) implements Event {

    @JsonCreator
    public UserRegisteredEvent {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId cannot be null or blank");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email cannot be null or blank");
        }
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalArgumentException("tenantId cannot be null or blank");
        }
        if (occurredAt == null) {
            throw new IllegalArgumentException("occurredAt cannot be null");
        }
    }

    /**
     * Creates a new UserRegisteredEvent with generated ID and current timestamp.
     */
    public UserRegisteredEvent(String userId, String email, String tenantId, String registrationSource) {
        this(
                UUID.randomUUID().toString(),
                userId,
                email,
                tenantId,
                Instant.now(),
                registrationSource
        );
    }

    @Override
    public String getEventId() {
        return eventId;
    }

    @Override
    public Instant getOccurredAt() {
        return occurredAt;
    }

    @Override
    public String getTenantId() {
        return tenantId;
    }
}