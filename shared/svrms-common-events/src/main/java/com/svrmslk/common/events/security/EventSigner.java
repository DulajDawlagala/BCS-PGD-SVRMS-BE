package com.svrmslk.common.events.security;

import com.svrmslk.common.events.api.Event;
import com.svrmslk.common.events.core.EventEnvelope;

/**
 * Signs event envelopes for authenticity and integrity verification.
 * Implementations can use HMAC, RSA, or other signing algorithms.
 *
 * @author Platform Team
 * @since 1.0.0
 */
public interface EventSigner {

    /**
     * Signs an event envelope by adding signature metadata.
     *
     * @param envelope the envelope to sign
     * @return the signed envelope with signature headers
     */
    <T extends Event> EventEnvelope<T> sign(EventEnvelope<T> envelope);
}