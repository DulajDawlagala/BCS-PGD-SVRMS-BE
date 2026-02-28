package com.svrmslk.authservice.authentication.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svrmslk.authservice.authentication.application.port.out.EventPublisherPort;
import com.svrmslk.common.events.core.EventHeaders;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
@ConditionalOnProperty(prefix = "kafka", name = "enabled", havingValue = "true")
public class KafkaEventPublisher implements EventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(KafkaEventPublisher.class);

    @Value("${kafka.topics.email:email-notification-requested}")
    private String topic;

    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaEventPublisher(
            KafkaTemplate<String, byte[]> kafkaTemplate,
            ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(DomainEvent event) {
        try {
            byte[] payload = objectMapper.writeValueAsBytes(event);

            UUID eventId = UUID.randomUUID();

            String key = event.getAggregateId() != null
                    ? String.valueOf(event.getAggregateId())
                    : eventId.toString();

            ProducerRecord<String, byte[]> record = new ProducerRecord<>(topic, key, payload);

            record.headers()
                    .add(EventHeaders.EVENT_ID,
                            eventId.toString().getBytes(StandardCharsets.UTF_8))
                    .add(EventHeaders.EVENT_TYPE,
                            event.getEventType().getBytes(StandardCharsets.UTF_8))
                    .add(EventHeaders.TENANT_ID,
                            "auth-service".getBytes(StandardCharsets.UTF_8));

            // ✅ Use CompletableFuture callback for Spring Kafka 3.x
            CompletableFuture<SendResult<String, byte[]>> future = kafkaTemplate.send(record);
            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to publish event {} to topic {}", eventId, topic, ex);
                } else {
                    log.debug("Published event {} to topic {}", eventId, topic);
                }
            });

        } catch (Exception e) {
            log.error("Failed to serialize or send event {}", event.getEventType(), e);
            throw new RuntimeException("Failed to publish event: " + event.getEventType(), e);
        }
    }
}

