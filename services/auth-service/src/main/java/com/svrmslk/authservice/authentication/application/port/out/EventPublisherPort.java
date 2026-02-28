package com.svrmslk.authservice.authentication.application.port.out;

public interface EventPublisherPort {

    void publish(DomainEvent event);

    interface DomainEvent {
        String getEventType();
        String getAggregateId();
    }
}