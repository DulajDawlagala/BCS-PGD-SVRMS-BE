package com.svrmslk.common.events.core;

import java.time.Instant;
import java.util.UUID;

public interface DomainEvent {

    UUID getEventId();

    UUID getAggregateId();

    String getEventType();

    Instant getOccurredAt();
}
