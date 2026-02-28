package com.svrmslk.common.events.api;

import java.time.Instant;

/**
 * Marker interface for all domain events in the system.
 * Events are immutable facts that have occurred in the domain.
 *
 * <p>Implementation guidelines:
 * <ul>
 *   <li>Events should be immutable (use records or final fields)</li>
 *   <li>Events should contain only data, no behavior</li>
 *   <li>Events represent past occurrences (use past tense naming)</li>
 * </ul>
 *
 * @author Platform Team
 * @since 1.0.0
 */
public interface Event {

    /**
     * Returns the unique identifier for this event instance.
     * This is typically generated at creation time.
     *
     * @return event unique identifier
     */
    String getEventId();

    /**
     * Returns the timestamp when this event occurred.
     *
     * @return event occurrence timestamp
     */
    Instant getOccurredAt();

    /**
     * Returns the tenant identifier for multi-tenant isolation.
     *
     * @return tenant identifier, or null for system-level events
     */
    String getTenantId();
}