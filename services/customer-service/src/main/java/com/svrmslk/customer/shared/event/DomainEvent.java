// FILE: shared/event/DomainEvent.java
package com.svrmslk.customer.shared.event;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class DomainEvent {

    private final String eventId;
    private final String eventType;
    private final String eventVersion;
    private final LocalDateTime occurredAt;
    private final EventMetadata metadata;

    protected DomainEvent(String eventType, String eventVersion) {
        this.eventId = UUID.randomUUID().toString();
        this.eventType = eventType;
        this.eventVersion = eventVersion;
        this.occurredAt = LocalDateTime.now();
        this.metadata = new EventMetadata();
    }

    public String getEventId() {
        return eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getEventVersion() {
        return eventVersion;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public EventMetadata getMetadata() {
        return metadata;
    }
}