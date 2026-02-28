package com.svrmslk.common.events.api;

import com.svrmslk.common.events.core.EventEnvelope;
import java.util.concurrent.CompletableFuture;

/**
 * Low-level producer abstraction for sending event envelopes.
 * This is the infrastructure port that adapters implement.
 *
 * <p>EventProducer implementations handle:
 * <ul>
 *   <li>Physical transport (Kafka, RabbitMQ, etc.)</li>
 *   <li>Serialization</li>
 *   <li>Delivery guarantees</li>
 *   <li>Partitioning strategy</li>
 * </ul>
 *
 * @author Platform Team
 * @since 1.0.0
 */
public interface EventProducer {

    /**
     * Sends an event envelope to the underlying messaging infrastructure.
     *
     * @param envelope the wrapped event with metadata
     * @return a future containing the send result
     */
    CompletableFuture<SendResult> send(EventEnvelope<?> envelope);

    /**
     * Sends an event envelope to a specific topic/channel.
     *
     * @param topic the destination topic
     * @param envelope the wrapped event with metadata
     * @return a future containing the send result
     */
    CompletableFuture<SendResult> send(String topic, EventEnvelope<?> envelope);

    /**
     * Sends an event envelope with a specific partition key.
     * Events with the same key are guaranteed to be ordered.
     *
     * @param topic the destination topic
     * @param partitionKey the partition key for ordering
     * @param envelope the wrapped event with metadata
     * @return a future containing the send result
     */
    CompletableFuture<SendResult> send(String topic, String partitionKey, EventEnvelope<?> envelope);

    /**
     * Result of a send operation.
     */
    record SendResult(
            String topic,
            String partition,
            long offset,
            long timestamp
    ) {}
}