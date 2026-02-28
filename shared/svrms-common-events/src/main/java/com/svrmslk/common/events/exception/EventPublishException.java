package com.svrmslk.common.events.exception;

/**
 * Exception thrown when event publishing fails.
 *
 * @author Platform Team
 * @since 1.0.0
 */
public class EventPublishException extends RuntimeException {

    private final String eventId;
    private final String eventType;

    public EventPublishException(String message) {
        super(message);
        this.eventId = null;
        this.eventType = null;
    }

    public EventPublishException(String message, Throwable cause) {
        super(message, cause);
        this.eventId = null;
        this.eventType = null;
    }

    public EventPublishException(String eventId, String eventType, String message, Throwable cause) {
        super(String.format("Failed to publish event [id=%s, type=%s]: %s", eventId, eventType, message), cause);
        this.eventId = eventId;
        this.eventType = eventType;
    }

    public String getEventId() {
        return eventId;
    }

    public String getEventType() {
        return eventType;
    }
}