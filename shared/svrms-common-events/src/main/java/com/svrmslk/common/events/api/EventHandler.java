package com.svrmslk.common.events.api;

import com.svrmslk.common.events.core.EventEnvelope;

/**
 * Functional interface for handling consumed events.
 * Implementations process specific event types and execute business logic.
 *
 * <p>Handlers should be:
 * <ul>
 *   <li>Idempotent - safe to execute multiple times with same input</li>
 *   <li>Fast - offload long-running tasks to async processors</li>
 *   <li>Stateless - don't rely on instance state</li>
 * </ul>
 *
 * @param <T> the event type this handler processes
 * @author Platform Team
 * @since 1.0.0
 */
@FunctionalInterface
public interface EventHandler<T extends Event> {

    /**
     * Handles a received event envelope.
     *
     * @param envelope the event envelope containing metadata and payload
     * @throws Exception if handling fails (will be caught by framework)
     */
    void handle(EventEnvelope<T> envelope) throws Exception;

    /**
     * Returns the event type this handler supports.
     * Used by the dispatcher to route events to the correct handler.
     *
     * @return the event class this handler processes
     */
    default Class<T> getEventType() {
        throw new UnsupportedOperationException("Handler must override getEventType() or use @EventHandlerMapping");
    }
}