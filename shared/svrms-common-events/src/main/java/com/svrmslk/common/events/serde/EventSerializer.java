package com.svrmslk.common.events.serde;

import com.svrmslk.common.events.core.EventEnvelope;

/**
 * Abstraction for event serialization.
 * Implementations handle conversion of EventEnvelope to bytes.
 *
 * @author Platform Team
 * @since 1.0.0
 */
public interface EventSerializer {

    /**
     * Serializes an event envelope to bytes.
     *
     * @param envelope the event envelope
     * @return serialized bytes
     * @throws SerializationException if serialization fails
     */
    byte[] serialize(EventEnvelope<?> envelope);

    /**
     * Exception thrown when serialization fails.
     */
    class SerializationException extends RuntimeException {
        public SerializationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}