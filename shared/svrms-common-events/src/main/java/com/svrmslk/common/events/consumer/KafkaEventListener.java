package com.svrmslk.common.events.consumer;

import com.svrmslk.common.events.core.EventEnvelope;
import com.svrmslk.common.events.core.EventHeaders;
import com.svrmslk.common.events.exception.EventConsumeException;
import com.svrmslk.common.events.observability.EventMetrics;
import com.svrmslk.common.events.observability.EventTracing;
import com.svrmslk.common.events.serde.EventDeserializer;
import com.svrmslk.common.events.security.EventVerifier;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;

import java.nio.charset.StandardCharsets;

/**
 * Kafka consumer listener that receives events and dispatches them to handlers.
 * Handles deserialization, security verification, metrics, and error handling.
 *
 * @author Platform Team
 * @since 1.0.0
 */
public class KafkaEventListener {

    private static final Logger log = LoggerFactory.getLogger(KafkaEventListener.class);

    private final EventDeserializer eventDeserializer;
    private final EventDispatcher eventDispatcher;
    private final EventVerifier eventVerifier;
    private final EventMetrics eventMetrics;
    private final EventTracing eventTracing;
    private final DeadLetterHandler deadLetterHandler;

    public KafkaEventListener(
            EventDeserializer eventDeserializer,
            EventDispatcher eventDispatcher,
            EventVerifier eventVerifier,
            EventMetrics eventMetrics,
            EventTracing eventTracing,
            DeadLetterHandler deadLetterHandler) {
        this.eventDeserializer = eventDeserializer;
        this.eventDispatcher = eventDispatcher;
        this.eventVerifier = eventVerifier;
        this.eventMetrics = eventMetrics;
        this.eventTracing = eventTracing;
        this.deadLetterHandler = deadLetterHandler;
    }

    /**
     * Main Kafka listener method.
     * Processes events with manual acknowledgment for better error handling.
     */
    @KafkaListener(
            topics = "${events.kafka.topics:events}",
            groupId = "${events.kafka.consumer.group-id:event-consumer-group}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(
            byte[] data,
            ConsumerRecord<String, byte[]> record,
            @org.springframework.messaging.handler.annotation.Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @org.springframework.messaging.handler.annotation.Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @org.springframework.messaging.handler.annotation.Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {

        long startTime = System.currentTimeMillis();
        String eventType = extractHeader(record, EventHeaders.EVENT_TYPE);
        String eventId = extractHeader(record, EventHeaders.EVENT_ID);
        String tenantId = extractHeader(record, EventHeaders.TENANT_ID);

        log.debug("Received event: topic={}, partition={}, offset={}, eventId={}, eventType={}, tenantId={}",
                topic, partition, offset, eventId, eventType, tenantId);

        try {
            // 1. Deserialize event
            EventEnvelope<?> envelope = eventDeserializer.deserialize(data, eventType);

            // 2. Start distributed trace
            String traceId = envelope.getTraceId();
            if (traceId != null) {
                eventTracing.continueTrace(traceId, envelope.metadata().correlationId());
            }

            // 3. Verify security signature
            if (!eventVerifier.verify(envelope)) {
                throw new EventConsumeException(
                        eventId, eventType,
                        "Event signature verification failed",
                        null, false
                );
            }

            // 4. Dispatch to handler
            eventDispatcher.dispatch(envelope);

            // 5. Acknowledge successful processing
            if (acknowledgment != null) {
                acknowledgment.acknowledge();
            }

            long duration = System.currentTimeMillis() - startTime;
            eventMetrics.recordConsume(eventType, duration, true);

            log.info("Event processed successfully: eventId={}, eventType={}, duration={}ms",
                    eventId, eventType, duration);

            if (traceId != null) {
                eventTracing.endTrace(traceId);
            }

        } catch (EventConsumeException ex) {
            handleConsumeError(ex, record, data, startTime, acknowledgment);
        } catch (Exception ex) {
            EventConsumeException consumeEx = new EventConsumeException(
                    eventId, eventType,
                    "Unexpected error processing event",
                    ex, true
            );
            handleConsumeError(consumeEx, record, data, startTime, acknowledgment);
        }
    }

    /**
     * Handles consumption errors with retry logic and dead letter queue.
     */
    private void handleConsumeError(
            EventConsumeException ex,
            ConsumerRecord<String, byte[]> record,
            byte[] data,
            long startTime,
            Acknowledgment acknowledgment) {

        long duration = System.currentTimeMillis() - startTime;
        String eventType = extractHeader(record, EventHeaders.EVENT_TYPE);

        eventMetrics.recordConsume(eventType, duration, false);

        log.error("Failed to process event: eventId={}, eventType={}, retryable={}",
                ex.getEventId(), ex.getEventType(), ex.isRetryable(), ex);

        // Check retry count
        int retryCount = extractRetryCount(record);
        int maxRetries = 3; // Should be configurable

        if (ex.isRetryable() && retryCount < maxRetries) {
            // Don't acknowledge - let Kafka retry
            log.warn("Event will be retried: eventId={}, attempt={}/{}",
                    ex.getEventId(), retryCount + 1, maxRetries);
            // In production, you might want to implement exponential backoff here
        } else {
            // Send to dead letter queue
            log.error("Moving event to dead letter queue: eventId={}, eventType={}",
                    ex.getEventId(), ex.getEventType());

            deadLetterHandler.handle(record, ex);

            // Acknowledge to move forward
            if (acknowledgment != null) {
                acknowledgment.acknowledge();
            }
        }
    }

    /**
     * Extracts a header value from the Kafka record.
     */
    private String extractHeader(ConsumerRecord<String, byte[]> record, String headerName) {
        org.apache.kafka.common.header.Header header = record.headers().lastHeader(headerName);
        if (header != null && header.value() != null) {
            return new String(header.value(), StandardCharsets.UTF_8);
        }
        return null;
    }

    /**
     * Extracts the retry count from headers.
     */
    private int extractRetryCount(ConsumerRecord<String, byte[]> record) {
        String retryCountStr = extractHeader(record, EventHeaders.RETRY_COUNT);
        if (retryCountStr != null) {
            try {
                return Integer.parseInt(retryCountStr);
            } catch (NumberFormatException ex) {
                log.warn("Invalid retry count header: {}", retryCountStr);
            }
        }
        return 0;
    }
}