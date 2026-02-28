package com.svrmslk.common.events.producer;

import com.svrmslk.common.events.api.Event;
import com.svrmslk.common.events.api.EventBus;
import com.svrmslk.common.events.api.EventProducer;
import com.svrmslk.common.events.core.EventEnvelope;
import com.svrmslk.common.events.core.EventMetadata;
import com.svrmslk.common.events.core.EventType;
import com.svrmslk.common.events.exception.EventPublishException;
import com.svrmslk.common.events.observability.EventMetrics;
import com.svrmslk.common.events.observability.EventTracing;
import com.svrmslk.common.events.schema.validation.JsonSchemaValidator;
import com.svrmslk.common.events.security.EventSigner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

/**
 * Default implementation of EventBus that orchestrates event publishing.
 * Handles validation, signing, metrics, tracing, and delegation to producer.
 *
 * @author Platform Team
 * @since 1.0.0
 */
public class DefaultEventBus implements EventBus {

    private static final Logger log = LoggerFactory.getLogger(DefaultEventBus.class);

    private final EventProducer eventProducer;
    private final JsonSchemaValidator schemaValidator;
    private final EventSigner eventSigner;
    private final EventMetrics eventMetrics;
    private final EventTracing eventTracing;
    private final String applicationName;

    public DefaultEventBus(
            EventProducer eventProducer,
            JsonSchemaValidator schemaValidator,
            EventSigner eventSigner,
            EventMetrics eventMetrics,
            EventTracing eventTracing,
            String applicationName) {
        this.eventProducer = eventProducer;
        this.schemaValidator = schemaValidator;
        this.eventSigner = eventSigner;
        this.eventMetrics = eventMetrics;
        this.eventTracing = eventTracing;
        this.applicationName = applicationName;
    }

    @Override
    public CompletableFuture<EventPublishResult> publish(Event event) {
        long startTime = System.currentTimeMillis();
        EventType eventType = EventType.fromClass(event.getClass());

        log.debug("Publishing event: eventId={}, type={}, tenantId={}",
                event.getEventId(), eventType, event.getTenantId());

        try {
            // 1. Validate schema
            schemaValidator.validate(event);

            // 2. Build envelope with metadata
            EventEnvelope<Event> envelope = buildEnvelope(event);

            // 3. Sign event for security
            EventEnvelope<Event> signedEnvelope = eventSigner.sign(envelope);

            // 4. Start distributed trace
            String traceId = eventTracing.startTrace(signedEnvelope);

            // 5. Publish via producer
            return eventProducer.send(signedEnvelope)
                    .thenApply(sendResult -> {
                        long duration = System.currentTimeMillis() - startTime;

                        // Record metrics
                        eventMetrics.recordPublish(eventType.value(), duration, true);

                        // End trace
                        eventTracing.endTrace(traceId);

                        log.info("Event published successfully: eventId={}, type={}, partition={}, offset={}, duration={}ms",
                                event.getEventId(), eventType, sendResult.partition(), sendResult.offset(), duration);

                        return new EventPublishResult(
                                event.getEventId(),
                                eventType.value(),
                                sendResult.partition(),
                                sendResult.offset(),
                                sendResult.timestamp()
                        );
                    })
                    .exceptionally(throwable -> {
                        long duration = System.currentTimeMillis() - startTime;

                        // Record failure metrics
                        eventMetrics.recordPublish(eventType.value(), duration, false);

                        // End trace with error
                        eventTracing.endTraceWithError(traceId, throwable);

                        log.error("Failed to publish event: eventId={}, type={}, duration={}ms",
                                event.getEventId(), eventType, duration, throwable);

                        throw new EventPublishException(
                                event.getEventId(),
                                eventType.value(),
                                "Event publishing failed",
                                throwable
                        );
                    });

        } catch (Exception ex) {
            long duration = System.currentTimeMillis() - startTime;
            eventMetrics.recordPublish(eventType.value(), duration, false);

            log.error("Failed to prepare event for publishing: eventId={}, type={}",
                    event.getEventId(), eventType, ex);

            return CompletableFuture.failedFuture(
                    new EventPublishException(
                            event.getEventId(),
                            eventType.value(),
                            "Event preparation failed",
                            ex
                    )
            );
        }
    }

    @Override
    public EventPublishResult publishSync(Event event) {
        try {
            return publish(event).join();
        } catch (Exception ex) {
            EventType eventType = EventType.fromClass(event.getClass());
            throw new EventPublishException(
                    event.getEventId(),
                    eventType.value(),
                    "Synchronous publish failed",
                    ex
            );
        }
    }

    /**
     * Builds an event envelope with complete metadata.
     */
    private <T extends Event> EventEnvelope<T> buildEnvelope(T event) {
        EventType eventType = EventType.fromClass(event.getClass());

        EventMetadata metadata = EventMetadata.builder()
                .eventId(event.getEventId())
                .eventType(eventType)
                .timestamp(event.getOccurredAt())
                .tenantId(event.getTenantId())
                .traceId(eventTracing.getCurrentTraceId())
                .correlationId(eventTracing.getCurrentCorrelationId())
                .source(applicationName)
                .build();

        return new EventEnvelope<>(metadata, event);
    }
}