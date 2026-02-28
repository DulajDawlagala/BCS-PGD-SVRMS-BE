package com.svrmslk.booking.shared.event;

import java.time.Instant;
import java.util.UUID;

public interface DomainEvent {
    String getEventType();
    UUID getAggregateId();
    Instant getOccurredAt();
    EventMetadata getMetadata();
}