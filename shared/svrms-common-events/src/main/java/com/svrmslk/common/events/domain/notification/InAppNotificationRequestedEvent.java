package com.svrmslk.common.events.domain.notification;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.svrmslk.common.events.api.Event;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Domain event emitted when an in-app notification is requested.
 * This event triggers the notification service to create an in-app notification.
 *
 * @author Platform Team
 * @since 1.0.0
 */
public record InAppNotificationRequestedEvent(
        @JsonProperty("eventId") String eventId,
        @JsonProperty("notificationId") String notificationId,
        @JsonProperty("tenantId") String tenantId,
        @JsonProperty("userId") String userId,
        @JsonProperty("title") String title,
        @JsonProperty("message") String message,
        @JsonProperty("notificationType") NotificationType notificationType,
        @JsonProperty("category") String category,
        @JsonProperty("priority") NotificationPriority priority,
        @JsonProperty("actionUrl") String actionUrl,
        @JsonProperty("actionLabel") String actionLabel,
        @JsonProperty("iconUrl") String iconUrl,
        @JsonProperty("expiresAt") Instant expiresAt,
        @JsonProperty("requiresAcknowledgment") boolean requiresAcknowledgment,
        @JsonProperty("data") Map<String, Object> data,
        @JsonProperty("requestedBy") String requestedBy,
        @JsonProperty("occurredAt") Instant occurredAt,
        @JsonProperty("metadata") Map<String, String> metadata
) implements Event {

    @JsonCreator
    public InAppNotificationRequestedEvent {
        // Validation
        if (eventId == null || eventId.isBlank()) {
            throw new IllegalArgumentException("eventId cannot be null or blank");
        }
        if (notificationId == null || notificationId.isBlank()) {
            throw new IllegalArgumentException("notificationId cannot be null or blank");
        }
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalArgumentException("tenantId cannot be null or blank");
        }
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId cannot be null or blank");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title cannot be null or blank");
        }
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("message cannot be null or blank");
        }
        if (occurredAt == null) {
            throw new IllegalArgumentException("occurredAt cannot be null");
        }

        // Defensive copies
        data = data == null ? Map.of() : Map.copyOf(data);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);

        // Defaults
        notificationType = notificationType == null ? NotificationType.INFO : notificationType;
        priority = priority == null ? NotificationPriority.NORMAL : priority;
        category = category == null ? "general" : category;
    }

    /**
     * Simple constructor for common use cases.
     */
    public InAppNotificationRequestedEvent(
            String notificationId,
            String tenantId,
            String userId,
            String title,
            String message,
            NotificationType notificationType,
            String requestedBy) {
        this(
                UUID.randomUUID().toString(),
                notificationId,
                tenantId,
                userId,
                title,
                message,
                notificationType,
                "general",
                NotificationPriority.NORMAL,
                null,
                null,
                null,
                null,
                false,
                Map.of(),
                requestedBy,
                Instant.now(),
                Map.of()
        );
    }

    @Override
    public String getEventId() {
        return eventId;
    }

    @Override
    public Instant getOccurredAt() {
        return occurredAt;
    }

    @Override
    public String getTenantId() {
        return tenantId;
    }

    /**
     * Builder for fluent event construction.
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String eventId = UUID.randomUUID().toString();
        private String notificationId = UUID.randomUUID().toString();
        private String tenantId;
        private String userId;
        private String title;
        private String message;
        private NotificationType notificationType = NotificationType.INFO;
        private String category = "general";
        private NotificationPriority priority = NotificationPriority.NORMAL;
        private String actionUrl;
        private String actionLabel;
        private String iconUrl;
        private Instant expiresAt;
        private boolean requiresAcknowledgment = false;
        private Map<String, Object> data = Map.of();
        private String requestedBy;
        private Instant occurredAt = Instant.now();
        private Map<String, String> metadata = Map.of();

        public Builder eventId(String eventId) {
            this.eventId = eventId;
            return this;
        }

        public Builder notificationId(String notificationId) {
            this.notificationId = notificationId;
            return this;
        }

        public Builder tenantId(String tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder notificationType(NotificationType notificationType) {
            this.notificationType = notificationType;
            return this;
        }

        public Builder category(String category) {
            this.category = category;
            return this;
        }

        public Builder priority(NotificationPriority priority) {
            this.priority = priority;
            return this;
        }

        public Builder actionUrl(String actionUrl) {
            this.actionUrl = actionUrl;
            return this;
        }

        public Builder actionLabel(String actionLabel) {
            this.actionLabel = actionLabel;
            return this;
        }

        public Builder iconUrl(String iconUrl) {
            this.iconUrl = iconUrl;
            return this;
        }

        public Builder expiresAt(Instant expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }

        public Builder requiresAcknowledgment(boolean requiresAcknowledgment) {
            this.requiresAcknowledgment = requiresAcknowledgment;
            return this;
        }

        public Builder data(Map<String, Object> data) {
            this.data = data;
            return this;
        }

        public Builder requestedBy(String requestedBy) {
            this.requestedBy = requestedBy;
            return this;
        }

        public Builder occurredAt(Instant occurredAt) {
            this.occurredAt = occurredAt;
            return this;
        }

        public Builder metadata(Map<String, String> metadata) {
            this.metadata = metadata;
            return this;
        }

        public InAppNotificationRequestedEvent build() {
            return new InAppNotificationRequestedEvent(
                    eventId, notificationId, tenantId, userId, title, message,
                    notificationType, category, priority, actionUrl, actionLabel,
                    iconUrl, expiresAt, requiresAcknowledgment, data, requestedBy,
                    occurredAt, metadata
            );
        }
    }

    /**
     * In-app notification types.
     */
    public enum NotificationType {
        INFO,
        SUCCESS,
        WARNING,
        ERROR,
        ALERT
    }

    /**
     * Notification priority levels.
     */
    public enum NotificationPriority {
        LOW,
        NORMAL,
        HIGH,
        URGENT
    }
}