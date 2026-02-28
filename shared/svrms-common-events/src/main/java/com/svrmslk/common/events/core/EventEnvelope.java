package com.svrmslk.common.events.core;

import com.svrmslk.common.events.api.Event;

/**
 * Wrapper that combines an event payload with its metadata.
 * The envelope pattern separates domain events from infrastructure concerns.
 *
 * @param <T> the event payload type
 * @author Platform Team
 * @since 1.0.0
 */
public record EventEnvelope<T extends Event>(
        EventMetadata metadata,
        T payload
) {

    public EventEnvelope {
        if (metadata == null) {
            throw new IllegalArgumentException("metadata cannot be null");
        }
        if (payload == null) {
            throw new IllegalArgumentException("payload cannot be null");
        }
    }

    /**
     * Creates an envelope from an event, deriving metadata.
     *
     * @param event the domain event
     * @param <E> event type
     * @return event envelope
     */
    public static <E extends Event> EventEnvelope<E> wrap(E event) {
        EventType eventType = EventType.fromClass(event.getClass());

        EventMetadata metadata = EventMetadata.builder()
                .eventId(event.getEventId())
                .eventType(eventType)
                .timestamp(event.getOccurredAt())
                .tenantId(event.getTenantId())
                .build();

        return new EventEnvelope<>(metadata, event);
    }

    /**
     * Creates an envelope with custom metadata.
     *
     * @param event the domain event
     * @param metadata the metadata
     * @param <E> event type
     * @return event envelope
     */
    public static <E extends Event> EventEnvelope<E> wrap(E event, EventMetadata metadata) {
        return new EventEnvelope<>(metadata, event);
    }

    public String getEventId() {
        return metadata.eventId();
    }

    public EventType getEventType() {
        return metadata.eventType();
    }

    public String getTenantId() {
        return metadata.tenantId();
    }

    public String getTraceId() {
        return metadata.traceId();
    }
}