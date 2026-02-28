package com.svrmslk.common.events.producer;

import com.svrmslk.common.events.api.EventProducer;
import com.svrmslk.common.events.core.EventEnvelope;
import com.svrmslk.common.events.exception.EventPublishException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Decorator that adds circuit breaker protection to an EventProducer.
 * Prevents cascading failures by failing fast when the downstream system is unhealthy.
 *
 * @author Platform Team
 * @since 1.0.0
 */
public class CircuitBreakerProducer implements EventProducer {

    private static final Logger log = LoggerFactory.getLogger(CircuitBreakerProducer.class);

    private final EventProducer delegate;
    private final CircuitBreaker circuitBreaker;

    public CircuitBreakerProducer(EventProducer delegate, CircuitBreaker circuitBreaker) {
        this.delegate = delegate;
        this.circuitBreaker = circuitBreaker;

        // Register state transition listeners
        circuitBreaker.getEventPublisher()
                .onStateTransition(event ->
                        log.warn("Circuit breaker state changed from {} to {}: {}",
                                event.getStateTransition().getFromState(),
                                event.getStateTransition().getToState(),
                                circuitBreaker.getMetrics()))
                .onError(event ->
                        log.debug("Circuit breaker recorded error: {}",
                                event.getThrowable().getMessage()))
                .onSuccess(event ->
                        log.debug("Circuit breaker recorded success"));
    }

    /**
     * Creates a CircuitBreakerProducer with default configuration:
     * - 50% failure rate threshold
     * - Minimum 10 calls before opening
     * - 30 second wait in open state
     * - 5 calls in half-open state
     */
    public static CircuitBreakerProducer withDefaults(EventProducer delegate, String instanceName) {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(50.0f)
                .slowCallRateThreshold(50.0f)
                .slowCallDurationThreshold(Duration.ofSeconds(2))
                .minimumNumberOfCalls(10)
                .slidingWindowSize(20)
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .waitDurationInOpenState(Duration.ofSeconds(30))
                .permittedNumberOfCallsInHalfOpenState(5)
                .automaticTransitionFromOpenToHalfOpenEnabled(true)
                .recordException(throwable -> throwable instanceof EventPublishException)
                .build();

        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
        CircuitBreaker circuitBreaker = registry.circuitBreaker(instanceName);

        return new CircuitBreakerProducer(delegate, circuitBreaker);
    }

    @Override
    public CompletableFuture<SendResult> send(EventEnvelope<?> envelope) {
        return executeWithCircuitBreaker(() -> delegate.send(envelope), envelope);
    }

    @Override
    public CompletableFuture<SendResult> send(String topic, EventEnvelope<?> envelope) {
        return executeWithCircuitBreaker(() -> delegate.send(topic, envelope), envelope);
    }

    @Override
    public CompletableFuture<SendResult> send(String topic, String partitionKey, EventEnvelope<?> envelope) {
        return executeWithCircuitBreaker(() -> delegate.send(topic, partitionKey, envelope), envelope);
    }

    private CompletableFuture<SendResult> executeWithCircuitBreaker(
            Supplier<CompletableFuture<SendResult>> operation,
            EventEnvelope<?> envelope) {

        try {
            // Check circuit breaker state before attempting
            if (circuitBreaker.getState() == CircuitBreaker.State.OPEN) {
                log.warn("Circuit breaker is OPEN, rejecting event publish: eventId={}, eventType={}",
                        envelope.getEventId(), envelope.getEventType());

                return CompletableFuture.failedFuture(
                        new EventPublishException(
                                envelope.getEventId(),
                                envelope.getEventType().value(),
                                "Circuit breaker is OPEN - downstream system is unavailable",
                                null
                        )
                );
            }

            // Decorate and execute
            Supplier<CompletableFuture<SendResult>> decoratedSupplier =
                    CircuitBreaker.decorateSupplier(circuitBreaker, operation);

            return decoratedSupplier.get()
                    .whenComplete((result, throwable) -> {
                        if (throwable != null) {
                            circuitBreaker.onError(0, java.util.concurrent.TimeUnit.NANOSECONDS, throwable);
                        } else {
                            circuitBreaker.onSuccess(0, java.util.concurrent.TimeUnit.NANOSECONDS);
                        }
                    });

        } catch (Exception ex) {
            log.error("Circuit breaker execution failed for event: eventId={}, eventType={}",
                    envelope.getEventId(), envelope.getEventType(), ex);

            return CompletableFuture.failedFuture(
                    new EventPublishException(
                            envelope.getEventId(),
                            envelope.getEventType().value(),
                            "Circuit breaker execution failed",
                            ex
                    )
            );
        }
    }

    /**
     * Gets the current circuit breaker metrics.
     */
    public CircuitBreaker.Metrics getMetrics() {
        return circuitBreaker.getMetrics();
    }

    /**
     * Gets the current circuit breaker state.
     */
    public CircuitBreaker.State getState() {
        return circuitBreaker.getState();
    }
}