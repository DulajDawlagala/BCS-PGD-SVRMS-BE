package com.svrmslk.common.events.exception;

import java.util.List;

/**
 * Exception thrown when event schema validation fails.
 *
 * @author Platform Team
 * @since 1.0.0
 */
public class SchemaValidationException extends RuntimeException {

    private final String eventType;
    private final List<String> validationErrors;

    public SchemaValidationException(String eventType, List<String> validationErrors) {
        super(String.format("Schema validation failed for event type '%s': %s",
                eventType, String.join(", ", validationErrors)));
        this.eventType = eventType;
        this.validationErrors = List.copyOf(validationErrors);
    }

    public SchemaValidationException(String eventType, String message) {
        super(String.format("Schema validation failed for event type '%s': %s", eventType, message));
        this.eventType = eventType;
        this.validationErrors = List.of(message);
    }

    public String getEventType() {
        return eventType;
    }

    public List<String> getValidationErrors() {
        return validationErrors;
    }
}