package com.svrmslk.authservice.authentication.infrastructure.messaging;

import com.svrmslk.authservice.authentication.application.port.out.EventPublisherPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "kafka", name = "enabled", havingValue = "false", matchIfMissing = true)
public class NoOpEventPublisher implements EventPublisherPort {

    private static final Logger logger = LoggerFactory.getLogger(NoOpEventPublisher.class);

    @Override
    public void publish(DomainEvent event) {
        logger.info("Event published (no-op): {} - {}", event.getEventType(), event.getAggregateId());
    }
}