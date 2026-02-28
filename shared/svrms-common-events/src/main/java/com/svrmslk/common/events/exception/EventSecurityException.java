package com.svrmslk.common.events.exception;

/**
 * Exception thrown when event security validation fails.
 *
 * @author Platform Team
 * @since 1.0.0
 */
public class EventSecurityException extends RuntimeException {

    private final String eventId;
    private final SecurityViolationType violationType;

    public EventSecurityException(String message) {
        this(null, SecurityViolationType.UNKNOWN, message, null);
    }

    public EventSecurityException(String eventId, SecurityViolationType violationType, String message) {
        this(eventId, violationType, message, null);
    }

    public EventSecurityException(String eventId, SecurityViolationType violationType, String message, Throwable cause) {
        super(String.format("Security violation for event [id=%s, type=%s]: %s",
                eventId, violationType, message), cause);
        this.eventId = eventId;
        this.violationType = violationType;
    }

    public String getEventId() {
        return eventId;
    }

    public SecurityViolationType getViolationType() {
        return violationType;
    }

    public enum SecurityViolationType {
        SIGNATURE_INVALID,
        SIGNATURE_MISSING,
        TENANT_MISMATCH,
        UNAUTHORIZED_SOURCE,
        UNKNOWN
    }
}