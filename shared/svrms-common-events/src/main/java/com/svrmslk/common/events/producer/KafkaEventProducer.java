package com.svrmslk.common.events.producer;

import com.svrmslk.common.events.api.EventProducer;
import com.svrmslk.common.events.core.EventEnvelope;
import com.svrmslk.common.events.core.EventHeaders;
import com.svrmslk.common.events.exception.EventPublishException;
import com.svrmslk.common.events.serde.EventSerializer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

/**
 * Kafka implementation of EventProducer.
 * Handles serialization, header population, and Kafka-specific concerns.
 *
 * @author Platform Team
 * @since 1.0.0
 */
public class KafkaEventProducer implements EventProducer {

    private static final Logger log = LoggerFactory.getLogger(KafkaEventProducer.class);

    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final EventSerializer eventSerializer;
    private final String defaultTopic;

    public KafkaEventProducer(
            KafkaTemplate<String, byte[]> kafkaTemplate,
            EventSerializer eventSerializer,
            String defaultTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.eventSerializer = eventSerializer;
        this.defaultTopic = defaultTopic;
    }

    @Override
    public CompletableFuture<SendResult> send(EventEnvelope<?> envelope) {
        return send(defaultTopic, envelope);
    }

    @Override
    public CompletableFuture<SendResult> send(String topic, EventEnvelope<?> envelope) {
        // Use tenantId as partition key for tenant isolation
        String partitionKey = envelope.getTenantId() != null
                ? envelope.getTenantId()
                : envelope.getEventId();
        return send(topic, partitionKey, envelope);
    }

    @Override
    public CompletableFuture<SendResult> send(String topic, String partitionKey, EventEnvelope<?> envelope) {
        try {
            // Serialize envelope to bytes
            byte[] payload = eventSerializer.serialize(envelope);

            // Build Kafka record with headers
            ProducerRecord<String, byte[]> record = new ProducerRecord<>(
                    topic,
                    null, // partition will be determined by key
                    partitionKey,
                    payload,
                    buildHeaders(envelope)
            );

            log.debug("Sending event to Kafka: topic={}, key={}, eventId={}, eventType={}",
                    topic, partitionKey, envelope.getEventId(), envelope.getEventType());

            // Send to Kafka and transform result
            return kafkaTemplate.send(record)
                    .thenApply(result -> {
                        var metadata = result.getRecordMetadata();
                        log.info("Event published successfully: topic={}, partition={}, offset={}, eventId={}",
                                metadata.topic(), metadata.partition(), metadata.offset(), envelope.getEventId());

                        return new SendResult(
                                metadata.topic(),
                                String.valueOf(metadata.partition()),
                                metadata.offset(),
                                metadata.timestamp()
                        );
                    })
                    .exceptionally(ex -> {
                        log.error("Failed to publish event: eventId={}, eventType={}",
                                envelope.getEventId(), envelope.getEventType(), ex);
                        throw new EventPublishException(
                                envelope.getEventId(),
                                envelope.getEventType().value(),
                                "Kafka send failed",
                                ex
                        );
                    });

        } catch (Exception ex) {
            log.error("Failed to serialize or send event: eventId={}, eventType={}",
                    envelope.getEventId(), envelope.getEventType(), ex);
            return CompletableFuture.failedFuture(
                    new EventPublishException(
                            envelope.getEventId(),
                            envelope.getEventType().value(),
                            "Serialization or send preparation failed",
                            ex
                    )
            );
        }
    }

    /**
     * Builds Kafka headers from event envelope metadata.
     */
    private Headers buildHeaders(EventEnvelope<?> envelope) {
        var metadata = envelope.metadata();
        var headers = new org.apache.kafka.common.header.internals.RecordHeaders();

        // Core headers
        addHeader(headers, EventHeaders.EVENT_ID, metadata.eventId());
        addHeader(headers, EventHeaders.EVENT_TYPE, metadata.eventType().value());
        addHeader(headers, EventHeaders.EVENT_VERSION, metadata.version().toString());
        addHeader(headers, EventHeaders.EVENT_TIMESTAMP, metadata.timestamp().toString());

        // Multi-tenancy
        if (metadata.tenantId() != null) {
            addHeader(headers, EventHeaders.TENANT_ID, metadata.tenantId());
        }

        // Distributed tracing
        if (metadata.traceId() != null) {
            addHeader(headers, EventHeaders.TRACE_ID, metadata.traceId());
        }
        if (metadata.correlationId() != null) {
            addHeader(headers, EventHeaders.CORRELATION_ID, metadata.correlationId());
        }
        if (metadata.causationId() != null) {
            addHeader(headers, EventHeaders.CAUSATION_ID, metadata.causationId());
        }

        // Source tracking
        if (metadata.userId() != null) {
            addHeader(headers, EventHeaders.USER_ID, metadata.userId());
        }
        if (metadata.source() != null) {
            addHeader(headers, EventHeaders.SOURCE, metadata.source());
        }

        // Custom headers
        metadata.customHeaders().forEach((key, value) -> addHeader(headers, key, value));

        return headers;
    }

    private void addHeader(Headers headers, String key, String value) {
        if (value != null) {
            headers.add(new RecordHeader(key, value.getBytes(StandardCharsets.UTF_8)));
        }
    }
}