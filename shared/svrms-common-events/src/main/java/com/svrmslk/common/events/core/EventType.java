package com.svrmslk.common.events.core;

import com.svrmslk.common.events.api.Event;

/**
 * Represents the type identifier for an event.
 * Used for routing, schema validation, and versioning.
 *
 * @author Platform Team
 * @since 1.0.0
 */
public record EventType(String value) {

    public EventType {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("EventType cannot be null or blank");
        }
        if (!value.matches("^[a-zA-Z][a-zA-Z0-9._-]*$")) {
            throw new IllegalArgumentException(
                    "EventType must start with letter and contain only alphanumeric, dot, hyphen, underscore: " + value
            );
        }
    }

    /**
     * Derives event type from event class name.
     * Example: UserRegisteredEvent -> user.registered
     *
     * @param eventClass the event class
     * @return derived event type
     */
    public static EventType fromClass(Class<? extends Event> eventClass) {
        String className = eventClass.getSimpleName();

        // Remove "Event" suffix if present
        if (className.endsWith("Event")) {
            className = className.substring(0, className.length() - 5);
        }

        // Convert CamelCase to dot.separated
        String typeValue = className
                .replaceAll("([a-z])([A-Z])", "$1.$2")
                .toLowerCase();

        return new EventType(typeValue);
    }

    /**
     * Creates an event type from a string value.
     *
     * @param value the type value
     * @return event type instance
     */
    public static EventType of(String value) {
        return new EventType(value);
    }

    @Override
    public String toString() {
        return value;
    }
}