//// FILE: shared/event/EventPublisher.java
//package com.svrmslk.customer.shared.event;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class EventPublisher {
//
//    private final KafkaTemplate<String, String> kafkaTemplate;
//    private final ObjectMapper objectMapper;
//
//    public void publish(DomainEvent event) {
//        try {
//            String topic = "customer-events";
//            String eventJson = objectMapper.writeValueAsString(event);
//
//            kafkaTemplate.send(topic, event.getEventId(), eventJson)
//                    .whenComplete((result, ex) -> {
//                        if (ex == null) {
//                            log.info("Published event: {} to topic: {}",
//                                    event.getEventType(), topic);
//                        } else {
//                            log.error("Failed to publish event: {}", event.getEventType(), ex);
//                        }
//                    });
//        } catch (Exception e) {
//            log.error("Error publishing event", e);
//        }
//    }
//}

// FILE: shared/event/EventPublisher.java
package com.svrmslk.customer.shared.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EventPublisher {

    public void publish(DomainEvent event) {
        // Kafka temporarily disabled
        log.debug(
                "Event publishing disabled. EventType={}, EventId={}",
                event.getEventType(),
                event.getEventId()
        );
    }
}
