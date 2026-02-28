package com.svrmslk.common.events.consumer;

import com.svrmslk.common.events.core.EventHeaders;
import com.svrmslk.common.events.exception.EventConsumeException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles failed events by sending them to a dead letter queue.
 * Preserves original headers and adds error metadata.
 *
 * @author Platform Team
 * @since 1.0.0
 */
public class DeadLetterHandler {

    private static final Logger log = LoggerFactory.getLogger(DeadLetterHandler.class);

    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final String deadLetterTopic;

    public DeadLetterHandler(
            KafkaTemplate<String, byte[]> kafkaTemplate,
            String deadLetterTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.deadLetterTopic = deadLetterTopic;
    }

    /**
     * Handles a failed event by sending it to the dead letter queue.
     *
     * @param failedRecord the original Kafka record that failed
     * @param exception the exception that caused the failure
     */
    public void handle(ConsumerRecord<String, byte[]> failedRecord, EventConsumeException exception) {
        try {
            log.info("Sending event to dead letter queue: topic={}, partition={}, offset={}, eventId={}",
                    failedRecord.topic(), failedRecord.partition(), failedRecord.offset(),
                    exception.getEventId());

            // Build DLQ record with enriched headers
            ProducerRecord<String, byte[]> dlqRecord = buildDlqRecord(failedRecord, exception);

            // Send to DLQ
            kafkaTemplate.send(dlqRecord)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to send event to dead letter queue: eventId={}",
                                    exception.getEventId(), ex);
                        } else {
                            log.info("Event sent to dead letter queue successfully: eventId={}, dlqOffset={}",
                                    exception.getEventId(), result.getRecordMetadata().offset());
                        }
                    });

        } catch (Exception ex) {
            log.error("Error handling dead letter for event: eventId={}",
                    exception.getEventId(), ex);
        }
    }

    /**
     * Builds a producer record for the dead letter queue.
     */
    private ProducerRecord<String, byte[]> buildDlqRecord(
            ConsumerRecord<String, byte[]> failedRecord,
            EventConsumeException exception) {

        // Copy original headers
        List<Header> headers = new ArrayList<>();
        failedRecord.headers().forEach(headers::add);

        // Add DLQ-specific headers
        addHeader(headers, EventHeaders.ORIGINAL_TOPIC, failedRecord.topic());
        addHeader(headers, EventHeaders.ERROR_MESSAGE, exception.getMessage());

        if (exception.getCause() != null) {
            addHeader(headers, EventHeaders.ERROR_CAUSE, exception.getCause().getClass().getName());
        }

        // Increment retry count
        int retryCount = extractRetryCount(failedRecord) + 1;
        addHeader(headers, EventHeaders.RETRY_COUNT, String.valueOf(retryCount));

        // Add timestamp of DLQ processing
        addHeader(headers, "dlq-timestamp", Instant.now().toString());

        // Create DLQ record
        ProducerRecord<String, byte[]> dlqRecord = new ProducerRecord<>(
                deadLetterTopic,
                null, // partition will be determined by key
                failedRecord.key(),
                failedRecord.value()
        );

        // Add all headers
        headers.forEach(header -> dlqRecord.headers().add(header));

        return dlqRecord;
    }

    /**
     * Extracts retry count from the original record.
     */
    private int extractRetryCount(ConsumerRecord<String, byte[]> record) {
        Header header = record.headers().lastHeader(EventHeaders.RETRY_COUNT);
        if (header != null && header.value() != null) {
            try {
                String value = new String(header.value(), StandardCharsets.UTF_8);
                return Integer.parseInt(value);
            } catch (NumberFormatException ex) {
                log.warn("Invalid retry count in header", ex);
            }
        }
        return 0;
    }

    /**
     * Adds or updates a header.
     */
    private void addHeader(List<Header> headers, String key, String value) {
        if (value != null) {
            // Remove existing header with same key
            headers.removeIf(h -> h.key().equals(key));
            // Add new header
            headers.add(new RecordHeader(key, value.getBytes(StandardCharsets.UTF_8)));
        }
    }
}