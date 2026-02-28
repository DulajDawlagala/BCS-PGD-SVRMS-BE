package com.svrmslk.authservice.authentication.application.usecase;

import com.svrmslk.authservice.authentication.application.port.out.EventPublisherPort;

public record UserLoggedInEvent(
        String userId,
        String email,
        String sessionId
) implements EventPublisherPort.DomainEvent {

    @Override
    public String getEventType() {
        return "UserLoggedIn";
    }

    @Override
    public String getAggregateId() {
        return userId;
    }
}
