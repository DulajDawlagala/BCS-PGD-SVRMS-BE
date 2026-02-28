package com.svrmslk.company.shared.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(String topic, DomainEvent event) {
        try {
            kafkaTemplate.send(topic, event.getAggregateId().toString(), event);
            log.info("Published event {} to topic {}", event.getEventType(), topic);
        } catch (Exception e) {
            log.error("Failed to publish event {} to topic {}", event.getEventType(), topic, e);
            throw new RuntimeException("Event publishing failed", e);
        }
    }
}