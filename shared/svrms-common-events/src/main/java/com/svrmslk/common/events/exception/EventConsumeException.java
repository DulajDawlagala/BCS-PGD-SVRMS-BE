package com.svrmslk.common.events.exception;

/**
 * Exception thrown when event consumption/handling fails.
 *
 * @author Platform Team
 * @since 1.0.0
 */
public class EventConsumeException extends RuntimeException {

    private final String eventId;
    private final String eventType;
    private final boolean retryable;

    public EventConsumeException(String message) {
        this(message, null, true);
    }

    public EventConsumeException(String message, Throwable cause) {
        this(message, cause, true);
    }

    public EventConsumeException(String message, Throwable cause, boolean retryable) {
        super(message, cause);
        this.eventId = null;
        this.eventType = null;
        this.retryable = retryable;
    }

    public EventConsumeException(String eventId, String eventType, String message, Throwable cause, boolean retryable) {
        super(String.format("Failed to consume event [id=%s, type=%s]: %s", eventId, eventType, message), cause);
        this.eventId = eventId;
        this.eventType = eventType;
        this.retryable = retryable;
    }

    public String getEventId() {
        return eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public boolean isRetryable() {
        return retryable;
    }
}