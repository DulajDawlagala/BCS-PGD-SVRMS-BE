package com.svrmslk.common.events.producer;

import com.svrmslk.common.events.api.EventProducer;
import com.svrmslk.common.events.core.EventEnvelope;
import com.svrmslk.common.events.exception.EventPublishException;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Decorator that adds retry capabilities to an EventProducer.
 * Uses Resilience4j for configurable retry logic with exponential backoff.
 *
 * @author Platform Team
 * @since 1.0.0
 */
public class RetryableEventProducer implements EventProducer {

    private static final Logger log = LoggerFactory.getLogger(RetryableEventProducer.class);

    private final EventProducer delegate;
    private final Retry retry;

    public RetryableEventProducer(EventProducer delegate, RetryConfig retryConfig) {
        this.delegate = delegate;
        this.retry = Retry.of("event-producer-retry", retryConfig);

        // Register event listeners for observability
        retry.getEventPublisher()
                .onRetry(event -> log.warn("Retrying event publish attempt {}: {}",
                        event.getNumberOfRetryAttempts(), event.getLastThrowable().getMessage()))
                .onSuccess(event -> log.debug("Event publish succeeded after {} attempts",
                        event.getNumberOfRetryAttempts()))
                .onError(event -> log.error("Event publish failed after {} attempts",
                        event.getNumberOfRetryAttempts(), event.getLastThrowable()));
    }

    /**
     * Creates a RetryableEventProducer with default configuration:
     * - Max 3 attempts
     * - Exponential backoff starting at 100ms
     * - Only retries on transient failures
     */
    public static RetryableEventProducer withDefaults(EventProducer delegate) {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofMillis(100))
                .retryOnException(RetryableEventProducer::isRetryableException)
                .build();

        return new RetryableEventProducer(delegate, config);
    }

    @Override
    public CompletableFuture<SendResult> send(EventEnvelope<?> envelope) {
        return executeWithRetry(() -> delegate.send(envelope));
    }

    @Override
    public CompletableFuture<SendResult> send(String topic, EventEnvelope<?> envelope) {
        return executeWithRetry(() -> delegate.send(topic, envelope));
    }

    @Override
    public CompletableFuture<SendResult> send(String topic, String partitionKey, EventEnvelope<?> envelope) {
        return executeWithRetry(() -> delegate.send(topic, partitionKey, envelope));
    }

    private CompletableFuture<SendResult> executeWithRetry(
            Supplier<CompletableFuture<SendResult>> operation) {

        Supplier<CompletableFuture<SendResult>> decoratedSupplier =
                Retry.decorateSupplier(retry, operation);

        return decoratedSupplier.get();
    }

    /**
     * Determines if an exception is retryable.
     * Non-retryable: serialization errors, validation errors
     * Retryable: network errors, timeouts, broker unavailable
     */
    private static boolean isRetryableException(Throwable throwable) {
        if (throwable instanceof EventPublishException publishEx) {
            Throwable cause = publishEx.getCause();
            if (cause == null) return false;

            String causeClass = cause.getClass().getName();

            // Kafka retryable exceptions
            return causeClass.contains("TimeoutException") ||
                    causeClass.contains("RetriableException") ||
                    causeClass.contains("NetworkException") ||
                    causeClass.contains("NotLeaderForPartitionException") ||
                    causeClass.contains("LeaderNotAvailableException");
        }

        return false;
    }
}