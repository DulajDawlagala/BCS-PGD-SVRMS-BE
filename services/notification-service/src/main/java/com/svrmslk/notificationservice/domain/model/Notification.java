// com/svrmslk/notificationservice/domain/model/Notification.java
package com.svrmslk.notificationservice.domain.model;

import java.time.Instant;
import java.util.Map;

public final class Notification {
    private final NotificationId id;
    private final String tenantId;
    private final String eventId;
    private final NotificationChannel channel;
    private final String recipient;
    private final String subject;
    private final String content;
    private final Map<String, String> metadata;
    private NotificationStatus status;
    private final Instant createdAt;
    private Instant updatedAt;
    private String failureReason;

    private Notification(
            NotificationId id,
            String tenantId,
            String eventId,
            NotificationChannel channel,
            String recipient,
            String subject,
            String content,
            Map<String, String> metadata,
            NotificationStatus status,
            Instant createdAt,
            Instant updatedAt,
            String failureReason
    ) {
        this.id = id;
        this.tenantId = tenantId;
        this.eventId = eventId;
        this.channel = channel;
        this.recipient = recipient;
        this.subject = subject;
        this.content = content;
        this.metadata = metadata != null ? Map.copyOf(metadata) : Map.of();
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.failureReason = failureReason;
    }

    public static Notification create(
            String tenantId,
            String eventId,
            NotificationChannel channel,
            String recipient,
            String subject,
            String content,
            Map<String, String> metadata
    ) {
        validateTenantId(tenantId);
        validateEventId(eventId);
        validateChannel(channel);
        validateRecipient(recipient, channel);
        validateSubject(subject, channel);
        validateContent(content);

        Instant now = Instant.now();
        return new Notification(
                NotificationId.generate(),
                tenantId,
                eventId,
                channel,
                recipient,
                subject,
                content,
                metadata,
                NotificationStatus.PENDING,
                now,
                now,
                null
        );
    }

    public void markAsSent() {
        transitionTo(NotificationStatus.SENT);
        this.updatedAt = Instant.now();
        this.failureReason = null;
    }

    public void markAsFailed(String reason) {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Failure reason cannot be null or blank");
        }
        transitionTo(NotificationStatus.FAILED);
        this.updatedAt = Instant.now();
        this.failureReason = reason;
    }

    private void transitionTo(NotificationStatus newStatus) {
        if (!this.status.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                    "Cannot transition from " + this.status + " to " + newStatus
            );
        }
        this.status = newStatus;
    }

    private static void validateTenantId(String tenantId) {
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalArgumentException("TenantId cannot be null or blank");
        }
    }

    private static void validateEventId(String eventId) {
        if (eventId == null || eventId.isBlank()) {
            throw new IllegalArgumentException("EventId cannot be null or blank");
        }
    }

    private static void validateChannel(NotificationChannel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("NotificationChannel cannot be null");
        }
    }

    private static void validateRecipient(String recipient, NotificationChannel channel) {
        if (recipient == null || recipient.isBlank()) {
            throw new IllegalArgumentException("Recipient cannot be null or blank");
        }
        if (channel == NotificationChannel.EMAIL && !isValidEmail(recipient)) {
            throw new IllegalArgumentException("Invalid email address: " + recipient);
        }
    }

    private static void validateSubject(String subject, NotificationChannel channel) {
        if (channel == NotificationChannel.EMAIL) {
            if (subject == null || subject.isBlank()) {
                throw new IllegalArgumentException("Subject cannot be null or blank for EMAIL notifications");
            }
        }
    }

    private static void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Content cannot be null or blank");
        }
    }

    private static boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    public NotificationId id() {
        return id;
    }

    public String tenantId() {
        return tenantId;
    }

    public String eventId() {
        return eventId;
    }

    public NotificationChannel channel() {
        return channel;
    }

    public String recipient() {
        return recipient;
    }

    public String subject() {
        return subject;
    }

    public String content() {
        return content;
    }

    public Map<String, String> metadata() {
        return metadata;
    }

    public NotificationStatus status() {
        return status;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public String failureReason() {
        return failureReason;
    }
}