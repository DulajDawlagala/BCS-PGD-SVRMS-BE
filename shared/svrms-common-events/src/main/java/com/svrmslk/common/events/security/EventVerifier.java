package com.svrmslk.common.events.security;

import com.svrmslk.common.events.core.EventEnvelope;

/**
 * Verifies event envelope signatures for authenticity and integrity.
 *
 * @author Platform Team
 * @since 1.0.0
 */
public interface EventVerifier {

    /**
     * Verifies the signature of an event envelope.
     *
     * @param envelope the envelope to verify
     * @return true if signature is valid, false otherwise
     */
    boolean verify(EventEnvelope<?> envelope);
}