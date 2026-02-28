package com.svrmslk.common.events.observability;

import com.svrmslk.common.events.api.EventProducer;
import com.svrmslk.common.events.core.EventEnvelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

/**
 * Interceptor that decorates EventProducer with metrics collection.
 * Uses decorator pattern to add observability without modifying core logic.
 *
 * @author Platform Team
 * @since 1.0.0
 */
public class MetricsInterceptor implements EventProducer {

    private static final Logger log = LoggerFactory.getLogger(MetricsInterceptor.class);

    private final EventProducer delegate;
    private final EventMetrics eventMetrics;

    public MetricsInterceptor(EventProducer delegate, EventMetrics eventMetrics) {
        this.delegate = delegate;
        this.eventMetrics = eventMetrics;
    }

    @Override
    public CompletableFuture<SendResult> send(EventEnvelope<?> envelope) {
        return measureSend(() -> delegate.send(envelope), envelope);
    }

    @Override
    public CompletableFuture<SendResult> send(String topic, EventEnvelope<?> envelope) {
        return measureSend(() -> delegate.send(topic, envelope), envelope);
    }

    @Override
    public CompletableFuture<SendResult> send(String topic, String partitionKey, EventEnvelope<?> envelope) {
        return measureSend(() -> delegate.send(topic, partitionKey, envelope), envelope);
    }

    /**
     * Measures the send operation and records metrics.
     */
    private CompletableFuture<SendResult> measureSend(
            java.util.function.Supplier<CompletableFuture<SendResult>> operation,
            EventEnvelope<?> envelope) {

        long startTime = System.currentTimeMillis();
        String eventType = envelope.getEventType().value();

        return operation.get()
                .whenComplete((result, throwable) -> {
                    long duration = System.currentTimeMillis() - startTime;
                    boolean success = throwable == null;

                    eventMetrics.recordPublish(eventType, duration, success);

                    if (success) {
                        log.trace("Send metrics recorded: eventType={}, duration={}ms", eventType, duration);
                    } else {
                        log.debug("Send failure metrics recorded: eventType={}, duration={}ms", eventType, duration);
                    }
                });
    }
}