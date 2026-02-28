package com.svrmslk.common.events.api;

import java.util.concurrent.CompletableFuture;

/**
 * Central abstraction for publishing domain events.
 * This interface decouples domain logic from infrastructure concerns.
 *
 * <p>The EventBus provides:
 * <ul>
 *   <li>Asynchronous publishing with CompletableFuture</li>
 *   <li>Infrastructure independence (adapter pattern)</li>
 *   <li>Transactional boundaries when needed</li>
 * </ul>
 *
 * <p>Usage example:
 * <pre>
 * eventBus.publish(new UserRegisteredEvent(userId, email))
 *         .thenAccept(result -> log.info("Event published: {}", result))
 *         .exceptionally(ex -> {
 *             log.error("Failed to publish event", ex);
 *             return null;
 *         });
 * </pre>
 *
 * @author Platform Team
 * @since 1.0.0
 */
public interface EventBus {

    /**
     * Publishes an event asynchronously to the event bus.
     *
     * @param event the domain event to publish
     * @return a future that completes when the event is successfully published
     * @throws com.svrmslk.common.events.exception.EventPublishException if publishing fails
     */
    CompletableFuture<EventPublishResult> publish(Event event);

    /**
     * Publishes an event synchronously, blocking until completion.
     * Use sparingly - prefer async publishing for better performance.
     *
     * @param event the domain event to publish
     * @return the publish result
     * @throws com.svrmslk.common.events.exception.EventPublishException if publishing fails
     */
    EventPublishResult publishSync(Event event);

    /**
     * Result of an event publish operation.
     */
    record EventPublishResult(
            String eventId,
            String eventType,
            String partition,
            long offset,
            long timestamp
    ) {}
}