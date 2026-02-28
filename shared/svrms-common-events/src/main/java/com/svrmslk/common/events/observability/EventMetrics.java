package com.svrmslk.common.events.observability;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Centralized metrics collection for event publishing and consumption.
 * Uses Micrometer for vendor-neutral metrics that work with Prometheus, Datadog, etc.
 *
 * @author Platform Team
 * @since 1.0.0
 */
public class EventMetrics {

    private static final Logger log = LoggerFactory.getLogger(EventMetrics.class);

    private static final String METRIC_PREFIX = "events";

    private final MeterRegistry meterRegistry;
    private final ConcurrentHashMap<String, Timer> publishTimers;
    private final ConcurrentHashMap<String, Timer> consumeTimers;
    private final ConcurrentHashMap<String, Counter> publishSuccessCounters;
    private final ConcurrentHashMap<String, Counter> publishFailureCounters;
    private final ConcurrentHashMap<String, Counter> consumeSuccessCounters;
    private final ConcurrentHashMap<String, Counter> consumeFailureCounters;

    public EventMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.publishTimers = new ConcurrentHashMap<>();
        this.consumeTimers = new ConcurrentHashMap<>();
        this.publishSuccessCounters = new ConcurrentHashMap<>();
        this.publishFailureCounters = new ConcurrentHashMap<>();
        this.consumeSuccessCounters = new ConcurrentHashMap<>();
        this.consumeFailureCounters = new ConcurrentHashMap<>();

        log.info("EventMetrics initialized with MeterRegistry: {}", meterRegistry.getClass().getSimpleName());
    }

    /**
     * Records a publish operation.
     *
     * @param eventType the event type
     * @param durationMs the duration in milliseconds
     * @param success whether the publish succeeded
     */
    public void recordPublish(String eventType, long durationMs, boolean success) {
        // Record latency
        Timer timer = publishTimers.computeIfAbsent(eventType, type ->
                Timer.builder(METRIC_PREFIX + ".publish.duration")
                        .description("Event publish duration")
                        .tag("event_type", type)
                        .register(meterRegistry)
        );
        timer.record(durationMs, TimeUnit.MILLISECONDS);

        // Record success/failure count
        if (success) {
            Counter counter = publishSuccessCounters.computeIfAbsent(eventType, type ->
                    Counter.builder(METRIC_PREFIX + ".publish.success")
                            .description("Successful event publishes")
                            .tag("event_type", type)
                            .register(meterRegistry)
            );
            counter.increment();
        } else {
            Counter counter = publishFailureCounters.computeIfAbsent(eventType, type ->
                    Counter.builder(METRIC_PREFIX + ".publish.failure")
                            .description("Failed event publishes")
                            .tag("event_type", type)
                            .register(meterRegistry)
            );
            counter.increment();
        }

        log.debug("Recorded publish metric: eventType={}, duration={}ms, success={}",
                eventType, durationMs, success);
    }

    /**
     * Records a consume operation.
     *
     * @param eventType the event type
     * @param durationMs the duration in milliseconds
     * @param success whether the consume succeeded
     */
    public void recordConsume(String eventType, long durationMs, boolean success) {
        // Record latency
        Timer timer = consumeTimers.computeIfAbsent(eventType, type ->
                Timer.builder(METRIC_PREFIX + ".consume.duration")
                        .description("Event consume duration")
                        .tag("event_type", type)
                        .register(meterRegistry)
        );
        timer.record(durationMs, TimeUnit.MILLISECONDS);

        // Record success/failure count
        if (success) {
            Counter counter = consumeSuccessCounters.computeIfAbsent(eventType, type ->
                    Counter.builder(METRIC_PREFIX + ".consume.success")
                            .description("Successful event consumes")
                            .tag("event_type", type)
                            .register(meterRegistry)
            );
            counter.increment();
        } else {
            Counter counter = consumeFailureCounters.computeIfAbsent(eventType, type ->
                    Counter.builder(METRIC_PREFIX + ".consume.failure")
                            .description("Failed event consumes")
                            .tag("event_type", type)
                            .register(meterRegistry)
            );
            counter.increment();
        }

        log.debug("Recorded consume metric: eventType={}, duration={}ms, success={}",
                eventType, durationMs, success);
    }

    /**
     * Records a schema validation event.
     */
    public void recordSchemaValidation(String eventType, boolean valid, long durationMs) {
        Timer timer = Timer.builder(METRIC_PREFIX + ".schema.validation.duration")
                .description("Schema validation duration")
                .tag("event_type", eventType)
                .tag("valid", String.valueOf(valid))
                .register(meterRegistry);
        timer.record(durationMs, TimeUnit.MILLISECONDS);

        Counter counter = Counter.builder(METRIC_PREFIX + ".schema.validation")
                .description("Schema validation count")
                .tag("event_type", eventType)
                .tag("valid", String.valueOf(valid))
                .register(meterRegistry);
        counter.increment();
    }

    /**
     * Records a dead letter queue event.
     */
    public void recordDeadLetter(String eventType, String reason) {
        Counter counter = Counter.builder(METRIC_PREFIX + ".deadletter")
                .description("Events sent to dead letter queue")
                .tag("event_type", eventType)
                .tag("reason", reason)
                .register(meterRegistry);
        counter.increment();

        log.warn("Event sent to dead letter queue: eventType={}, reason={}", eventType, reason);
    }

    /**
     * Records a circuit breaker state change.
     */
    public void recordCircuitBreakerStateChange(String state) {
        Counter counter = Counter.builder(METRIC_PREFIX + ".circuit_breaker.state_change")
                .description("Circuit breaker state changes")
                .tag("state", state)
                .register(meterRegistry);
        counter.increment();
    }

    /**
     * Records a retry attempt.
     */
    public void recordRetry(String eventType, int attemptNumber) {
        Counter counter = Counter.builder(METRIC_PREFIX + ".retry")
                .description("Event publish retry attempts")
                .tag("event_type", eventType)
                .tag("attempt", String.valueOf(attemptNumber))
                .register(meterRegistry);
        counter.increment();
    }

    /**
     * Gets the total publish count for an event type.
     */
    public double getPublishCount(String eventType) {
        double success = publishSuccessCounters.getOrDefault(eventType,
                Counter.builder("dummy").register(meterRegistry)).count();
        double failure = publishFailureCounters.getOrDefault(eventType,
                Counter.builder("dummy").register(meterRegistry)).count();
        return success + failure;
    }

    /**
     * Gets the publish success rate for an event type.
     */
    public double getPublishSuccessRate(String eventType) {
        double success = publishSuccessCounters.getOrDefault(eventType,
                Counter.builder("dummy").register(meterRegistry)).count();
        double total = getPublishCount(eventType);
        return total > 0 ? (success / total) * 100.0 : 0.0;
    }
}