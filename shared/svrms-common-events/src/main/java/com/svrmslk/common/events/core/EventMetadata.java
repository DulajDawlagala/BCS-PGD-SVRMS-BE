package com.svrmslk.common.events.core;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Metadata associated with an event.
 * Contains information about the event's context, origin, and processing requirements.
 *
 * @author Platform Team
 * @since 1.0.0
 */
public record EventMetadata(
        String eventId,
        EventType eventType,
        EventVersion version,
        Instant timestamp,
        String tenantId,
        String traceId,
        String correlationId,
        String causationId,
        String userId,
        String source,
        Map<String, String> customHeaders
) {

    public EventMetadata {
        if (eventId == null || eventId.isBlank()) {
            throw new IllegalArgumentException("eventId cannot be null or blank");
        }
        if (eventType == null) {
            throw new IllegalArgumentException("eventType cannot be null");
        }
        if (version == null) {
            throw new IllegalArgumentException("version cannot be null");
        }
        if (timestamp == null) {
            throw new IllegalArgumentException("timestamp cannot be null");
        }

        // Defensive copy of mutable map
        customHeaders = customHeaders == null ? Map.of() : Map.copyOf(customHeaders);
    }

    /**
     * Builder for fluent metadata construction.
     */
    public static class Builder {
        private String eventId = UUID.randomUUID().toString();
        private EventType eventType;
        private EventVersion version = EventVersion.V1_0_0;
        private Instant timestamp = Instant.now();
        private String tenantId;
        private String traceId;
        private String correlationId;
        private String causationId;
        private String userId;
        private String source;
        private Map<String, String> customHeaders = Map.of();

        public Builder eventId(String eventId) {
            this.eventId = eventId;
            return this;
        }

        public Builder eventType(EventType eventType) {
            this.eventType = eventType;
            return this;
        }

        public Builder version(EventVersion version) {
            this.version = version;
            return this;
        }

        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder tenantId(String tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        public Builder traceId(String traceId) {
            this.traceId = traceId;
            return this;
        }

        public Builder correlationId(String correlationId) {
            this.correlationId = correlationId;
            return this;
        }

        public Builder causationId(String causationId) {
            this.causationId = causationId;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder source(String source) {
            this.source = source;
            return this;
        }

        public Builder customHeaders(Map<String, String> customHeaders) {
            this.customHeaders = customHeaders;
            return this;
        }

        public EventMetadata build() {
            return new EventMetadata(
                    eventId, eventType, version, timestamp, tenantId,
                    traceId, correlationId, causationId, userId, source, customHeaders
            );
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}