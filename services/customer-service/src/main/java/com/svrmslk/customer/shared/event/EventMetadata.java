// FILE: shared/event/EventMetadata.java
package com.svrmslk.customer.shared.event;

import java.time.LocalDateTime;

public class EventMetadata {

    private final LocalDateTime timestamp;
    private final String source;
    private final String correlationId;

    public EventMetadata() {
        this.timestamp = LocalDateTime.now();
        this.source = "customer-service";
        this.correlationId = java.util.UUID.randomUUID().toString();
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getSource() {
        return source;
    }

    public String getCorrelationId() {
        return correlationId;
    }
}