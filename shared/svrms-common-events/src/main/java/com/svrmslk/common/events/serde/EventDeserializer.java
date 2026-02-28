package com.svrmslk.common.events.serde;

import com.svrmslk.common.events.core.EventEnvelope;

/**
 * Abstraction for event deserialization.
 * Implementations handle conversion of bytes to EventEnvelope.
 *
 * @author Platform Team
 * @since 1.0.0
 */
public interface EventDeserializer {

    /**
     * Deserializes bytes to an event envelope.
     *
     * @param data the serialized bytes
     * @param eventType the expected event type (for polymorphic deserialization)
     * @return deserialized event envelope
     * @throws DeserializationException if deserialization fails
     */
    EventEnvelope<?> deserialize(byte[] data, String eventType);

    /**
     * Exception thrown when deserialization fails.
     */
    class DeserializationException extends RuntimeException {
        public DeserializationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}