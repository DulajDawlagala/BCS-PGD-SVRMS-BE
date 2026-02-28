// com/svrmslk/notificationservice/domain/event/NotificationSentEvent.java
package com.svrmslk.notificationservice.domain.event;

import com.svrmslk.notificationservice.domain.model.NotificationChannel;
import com.svrmslk.notificationservice.domain.model.NotificationId;

import java.time.Instant;

public record NotificationSentEvent(
        NotificationId notificationId,
        String tenantId,
        String eventId,
        NotificationChannel channel,
        String recipient,
        Instant sentAt
) {
    public NotificationSentEvent {
        if (notificationId == null) {
            throw new IllegalArgumentException("NotificationId cannot be null");
        }
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalArgumentException("TenantId cannot be null or blank");
        }
        if (eventId == null || eventId.isBlank()) {
            throw new IllegalArgumentException("EventId cannot be null or blank");
        }
        if (channel == null) {
            throw new IllegalArgumentException("Channel cannot be null");
        }
        if (recipient == null || recipient.isBlank()) {
            throw new IllegalArgumentException("Recipient cannot be null or blank");
        }
        if (sentAt == null) {
            throw new IllegalArgumentException("SentAt cannot be null");
        }
    }
}