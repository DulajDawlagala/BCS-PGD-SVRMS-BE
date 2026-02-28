package com.svrmslk.common.events.observability;

import com.svrmslk.common.events.core.EventEnvelope;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Distributed tracing support for events using Micrometer Tracing.
 * Integrates with OpenTelemetry, Zipkin, Jaeger, etc.
 *
 * @author Platform Team
 * @since 1.0.0
 */
public class EventTracing {

    private static final Logger log = LoggerFactory.getLogger(EventTracing.class);

    private final Tracer tracer;
    private final ThreadLocal<String> currentTraceId;
    private final ThreadLocal<String> currentCorrelationId;

    public EventTracing(Tracer tracer) {
        this.tracer = tracer;
        this.currentTraceId = new ThreadLocal<>();
        this.currentCorrelationId = new ThreadLocal<>();

        log.info("EventTracing initialized with Tracer: {}", tracer.getClass().getSimpleName());
    }

    /**
     * Starts a new trace for an event envelope.
     *
     * @param envelope the event envelope
     * @return the trace ID
     */
    public String startTrace(EventEnvelope<?> envelope) {
        String traceId = envelope.getTraceId();
        if (traceId == null) {
            traceId = generateTraceId();
        }

        Span span = tracer.nextSpan().name("event.publish")
                .tag("event.id", envelope.getEventId())
                .tag("event.type", envelope.getEventType().value())
                .tag("event.version", envelope.metadata().version().toString());

        if (envelope.getTenantId() != null) {
            span.tag("tenant.id", envelope.getTenantId());
        }

        span.start();

        currentTraceId.set(traceId);
        if (envelope.metadata().correlationId() != null) {
            currentCorrelationId.set(envelope.metadata().correlationId());
        }

        log.debug("Started trace: traceId={}, eventId={}, eventType={}",
                traceId, envelope.getEventId(), envelope.getEventType());

        return traceId;
    }

    /**
     * Continues an existing trace from consumed event.
     *
     * @param traceId the trace ID to continue
     * @param correlationId the correlation ID
     */
    public void continueTrace(String traceId, String correlationId) {
        currentTraceId.set(traceId);
        currentCorrelationId.set(correlationId);

        Span span = tracer.nextSpan().name("event.consume");
        span.start();

        log.debug("Continued trace: traceId={}, correlationId={}", traceId, correlationId);
    }

    /**
     * Ends the current trace successfully.
     *
     * @param traceId the trace ID
     */
    public void endTrace(String traceId) {
        Span currentSpan = tracer.currentSpan();
        if (currentSpan != null) {
            currentSpan.end();
        }

        currentTraceId.remove();
        currentCorrelationId.remove();

        log.debug("Ended trace: traceId={}", traceId);
    }

    /**
     * Ends the current trace with an error.
     *
     * @param traceId the trace ID
     * @param error the error that occurred
     */
    public void endTraceWithError(String traceId, Throwable error) {
        Span currentSpan = tracer.currentSpan();
        if (currentSpan != null) {
            currentSpan.error(error);
            currentSpan.end();
        }

        currentTraceId.remove();
        currentCorrelationId.remove();

        log.debug("Ended trace with error: traceId={}, error={}", traceId, error.getMessage());
    }

    /**
     * Adds a tag to the current span.
     */
    public void addTag(String key, String value) {
        Span currentSpan = tracer.currentSpan();
        if (currentSpan != null) {
            currentSpan.tag(key, value);
        }
    }

    /**
     * Adds an event to the current span.
     */
    public void addEvent(String eventName) {
        Span currentSpan = tracer.currentSpan();
        if (currentSpan != null) {
            currentSpan.event(eventName);
        }
    }

    /**
     * Gets the current trace ID.
     */
    public String getCurrentTraceId() {
        return currentTraceId.get();
    }

    /**
     * Gets the current correlation ID.
     */
    public String getCurrentCorrelationId() {
        return currentCorrelationId.get();
    }

    /**
     * Generates a new trace ID.
     */
    private String generateTraceId() {
        return UUID.randomUUID().toString();
    }
}