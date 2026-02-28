// com/svrmslk/notificationservice/application/port/in/EmailNotificationCommand.java
package com.svrmslk.notificationservice.application.port.in;

import java.util.Map;

public record EmailNotificationCommand(
        String tenantId,
        String eventId,
        String recipient,
        String subject,
        String content,
        Map<String, String> metadata
) {
    public EmailNotificationCommand {
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalArgumentException("TenantId cannot be null or blank");
        }
        if (eventId == null || eventId.isBlank()) {
            throw new IllegalArgumentException("EventId cannot be null or blank");
        }
        if (recipient == null || recipient.isBlank()) {
            throw new IllegalArgumentException("Recipient cannot be null or blank");
        }
        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("Subject cannot be null or blank");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Content cannot be null or blank");
        }
        metadata = metadata != null ? Map.copyOf(metadata) : Map.of();
    }
}