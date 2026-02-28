//package com.svrmslk.common.events.domain.notification;
//
//import com.fasterxml.jackson.annotation.JsonCreator;
//import com.fasterxml.jackson.annotation.JsonProperty;
//import com.svrmslk.common.events.api.Event;
//
//import java.time.Instant;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//
///**
// * Domain event emitted when an email notification is requested.
// * This event triggers the notification service to send an email.
// *
// * @author Platform Team
// * @since 1.0.0
// */
//public record EmailNotificationRequestedEvent(
//        @JsonProperty("eventId") String eventId,
//        @JsonProperty("notificationId") String notificationId,
//        @JsonProperty("tenantId") String tenantId,
//        @JsonProperty("recipientEmail") String recipientEmail,
//        @JsonProperty("recipientName") String recipientName,
//        @JsonProperty("subject") String subject,
//        @JsonProperty("templateId") String templateId,
//        @JsonProperty("templateVariables") Map<String, Object> templateVariables,
//        @JsonProperty("priority") NotificationPriority priority,
//        @JsonProperty("ccEmails") List<String> ccEmails,
//        @JsonProperty("bccEmails") List<String> bccEmails,
//        @JsonProperty("attachments") List<EmailAttachment> attachments,
//        @JsonProperty("replyTo") String replyTo,
//        @JsonProperty("requestedBy") String requestedBy,
//        @JsonProperty("occurredAt") Instant occurredAt,
//        @JsonProperty("metadata") Map<String, String> metadata
//) implements Event {
//
//    @JsonCreator
//    public EmailNotificationRequestedEvent {
//        // Validation
//        if (eventId == null || eventId.isBlank()) {
//            throw new IllegalArgumentException("eventId cannot be null or blank");
//        }
//        if (notificationId == null || notificationId.isBlank()) {
//            throw new IllegalArgumentException("notificationId cannot be null or blank");
//        }
//        if (tenantId == null || tenantId.isBlank()) {
//            throw new IllegalArgumentException("tenantId cannot be null or blank");
//        }
//        if (recipientEmail == null || recipientEmail.isBlank()) {
//            throw new IllegalArgumentException("recipientEmail cannot be null or blank");
//        }
//        if (!isValidEmail(recipientEmail)) {
//            throw new IllegalArgumentException("recipientEmail is not valid: " + recipientEmail);
//        }
//        if (subject == null || subject.isBlank()) {
//            throw new IllegalArgumentException("subject cannot be null or blank");
//        }
//        if (templateId == null || templateId.isBlank()) {
//            throw new IllegalArgumentException("templateId cannot be null or blank");
//        }
//        if (occurredAt == null) {
//            throw new IllegalArgumentException("occurredAt cannot be null");
//        }
//
//        // Defensive copies
//        templateVariables = templateVariables == null ? Map.of() : Map.copyOf(templateVariables);
//        ccEmails = ccEmails == null ? List.of() : List.copyOf(ccEmails);
//        bccEmails = bccEmails == null ? List.of() : List.copyOf(bccEmails);
//        attachments = attachments == null ? List.of() : List.copyOf(attachments);
//        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
//
//        // Default priority
//        priority = priority == null ? NotificationPriority.NORMAL : priority;
//    }
//
//    /**
//     * Builder constructor for easy creation.
//     */
//    public EmailNotificationRequestedEvent(
//            String notificationId,
//            String tenantId,
//            String recipientEmail,
//            String recipientName,
//            String subject,
//            String templateId,
//            Map<String, Object> templateVariables,
//            String requestedBy) {
//        this(
//                UUID.randomUUID().toString(),
//                notificationId,
//                tenantId,
//                recipientEmail,
//                recipientName,
//                subject,
//                templateId,
//                templateVariables,
//                NotificationPriority.NORMAL,
//                List.of(),
//                List.of(),
//                List.of(),
//                null,
//                requestedBy,
//                Instant.now(),
//                Map.of()
//        );
//    }
//
//    @Override
//    public String getEventId() {
//        return eventId;
//    }
//
//    @Override
//    public Instant getOccurredAt() {
//        return occurredAt;
//    }
//
//    @Override
//    public String getTenantId() {
//        return tenantId;
//    }
//
//    /**
//     * Simple email validation.
//     */
//    private static boolean isValidEmail(String email) {
//        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
//    }
//
//    /**
//     * Builder for fluent event construction.
//     */
//    public static Builder builder() {
//        return new Builder();
//    }
//
//    public static class Builder {
//        private String eventId = UUID.randomUUID().toString();
//        private String notificationId = UUID.randomUUID().toString();
//        private String tenantId;
//        private String recipientEmail;
//        private String recipientName;
//        private String subject;
//        private String templateId;
//        private Map<String, Object> templateVariables = Map.of();
//        private NotificationPriority priority = NotificationPriority.NORMAL;
//        private List<String> ccEmails = List.of();
//        private List<String> bccEmails = List.of();
//        private List<EmailAttachment> attachments = List.of();
//        private String replyTo;
//        private String requestedBy;
//        private Instant occurredAt = Instant.now();
//        private Map<String, String> metadata = Map.of();
//
//        public Builder eventId(String eventId) {
//            this.eventId = eventId;
//            return this;
//        }
//
//        public Builder notificationId(String notificationId) {
//            this.notificationId = notificationId;
//            return this;
//        }
//
//        public Builder tenantId(String tenantId) {
//            this.tenantId = tenantId;
//            return this;
//        }
//
//        public Builder recipientEmail(String recipientEmail) {
//            this.recipientEmail = recipientEmail;
//            return this;
//        }
//
//        public Builder recipientName(String recipientName) {
//            this.recipientName = recipientName;
//            return this;
//        }
//
//        public Builder subject(String subject) {
//            this.subject = subject;
//            return this;
//        }
//
//        public Builder templateId(String templateId) {
//            this.templateId = templateId;
//            return this;
//        }
//
//        public Builder templateVariables(Map<String, Object> templateVariables) {
//            this.templateVariables = templateVariables;
//            return this;
//        }
//
//        public Builder priority(NotificationPriority priority) {
//            this.priority = priority;
//            return this;
//        }
//
//        public Builder ccEmails(List<String> ccEmails) {
//            this.ccEmails = ccEmails;
//            return this;
//        }
//
//        public Builder bccEmails(List<String> bccEmails) {
//            this.bccEmails = bccEmails;
//            return this;
//        }
//
//        public Builder attachments(List<EmailAttachment> attachments) {
//            this.attachments = attachments;
//            return this;
//        }
//
//        public Builder replyTo(String replyTo) {
//            this.replyTo = replyTo;
//            return this;
//        }
//
//        public Builder requestedBy(String requestedBy) {
//            this.requestedBy = requestedBy;
//            return this;
//        }
//
//        public Builder occurredAt(Instant occurredAt) {
//            this.occurredAt = occurredAt;
//            return this;
//        }
//
//        public Builder metadata(Map<String, String> metadata) {
//            this.metadata = metadata;
//            return this;
//        }
//
//        public EmailNotificationRequestedEvent build() {
//            return new EmailNotificationRequestedEvent(
//                    eventId, notificationId, tenantId, recipientEmail, recipientName,
//                    subject, templateId, templateVariables, priority, ccEmails, bccEmails,
//                    attachments, replyTo, requestedBy, occurredAt, metadata
//            );
//        }
//    }
//
//    /**
//     * Email attachment details.
//     */
//    public record EmailAttachment(
//            @JsonProperty("fileName") String fileName,
//            @JsonProperty("contentType") String contentType,
//            @JsonProperty("size") long size,
//            @JsonProperty("url") String url
//    ) {
//        @JsonCreator
//        public EmailAttachment {
//            if (fileName == null || fileName.isBlank()) {
//                throw new IllegalArgumentException("fileName cannot be null or blank");
//            }
//            if (contentType == null || contentType.isBlank()) {
//                throw new IllegalArgumentException("contentType cannot be null or blank");
//            }
//            if (size <= 0) {
//                throw new IllegalArgumentException("size must be positive");
//            }
//        }
//    }
//
//    /**
//     * Notification priority levels.
//     */
//    public enum NotificationPriority {
//        LOW,
//        NORMAL,
//        HIGH,
//        URGENT
//    }
//}

package com.svrmslk.common.events.domain.notification;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.svrmslk.common.events.api.Event;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Domain event emitted when an email notification is requested.
 * This event triggers the notification service to send an email.
 *
 * @author Platform Team
 * @since 1.0.0
 */
public record EmailNotificationRequestedEvent(
        @JsonProperty("eventId") String eventId,
        @JsonProperty("notificationId") String notificationId,
        @JsonProperty("tenantId") String tenantId,
        @JsonProperty("recipientEmail") String recipientEmail,
        @JsonProperty("recipientName") String recipientName,
        @JsonProperty("subject") String subject,
        @JsonProperty("templateId") String templateId,
        @JsonProperty("templateVariables") Map<String, Object> templateVariables,
        @JsonProperty("priority") NotificationPriority priority,
        @JsonProperty("ccEmails") List<String> ccEmails,
        @JsonProperty("bccEmails") List<String> bccEmails,
        @JsonProperty("attachments") List<EmailAttachment> attachments,
        @JsonProperty("replyTo") String replyTo,
        @JsonProperty("requestedBy") String requestedBy,
        @JsonProperty("occurredAt") Instant occurredAt,
        @JsonProperty("metadata") Map<String, String> metadata
) implements Event {

    @JsonCreator
    public EmailNotificationRequestedEvent(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("notificationId") String notificationId,
            @JsonProperty("tenantId") String tenantId,
            @JsonProperty("recipientEmail") String recipientEmail,
            @JsonProperty("recipientName") String recipientName,
            @JsonProperty("subject") String subject,
            @JsonProperty("templateId") String templateId,
            @JsonProperty("templateVariables") Map<String, Object> templateVariables,
            @JsonProperty("priority") NotificationPriority priority,
            @JsonProperty("ccEmails") List<String> ccEmails,
            @JsonProperty("bccEmails") List<String> bccEmails,
            @JsonProperty("attachments") List<EmailAttachment> attachments,
            @JsonProperty("replyTo") String replyTo,
            @JsonProperty("requestedBy") String requestedBy,
            @JsonProperty("occurredAt") Instant occurredAt,
            @JsonProperty("metadata") Map<String, String> metadata) {

        // Generate defaults for missing values
        this.eventId = (eventId == null || eventId.isBlank()) ? UUID.randomUUID().toString() : eventId;
        this.notificationId = (notificationId == null || notificationId.isBlank()) ? UUID.randomUUID().toString() : notificationId;

        // Validation for required fields
        if (tenantId == null || tenantId.isBlank()) {
            this.tenantId = "default"; // Fallback to default tenant if not provided
        } else {
            this.tenantId = tenantId;
        }

        if (recipientEmail == null || recipientEmail.isBlank()) {
            throw new IllegalArgumentException("recipientEmail cannot be null or blank");
        }
        if (!isValidEmail(recipientEmail)) {
            throw new IllegalArgumentException("recipientEmail is not valid: " + recipientEmail);
        }
        this.recipientEmail = recipientEmail;

        this.recipientName = (recipientName == null || recipientName.isBlank()) ? recipientEmail : recipientName;

        if (subject == null || subject.isBlank()) {
            this.subject = "Notification"; // Default subject
        } else {
            this.subject = subject;
        }

        if (templateId == null || templateId.isBlank()) {
            throw new IllegalArgumentException("templateId cannot be null or blank");
        }
        this.templateId = templateId;

        this.occurredAt = (occurredAt == null) ? Instant.now() : occurredAt;

        // Defensive copies with null handling
        this.templateVariables = templateVariables == null ? Map.of() : Map.copyOf(templateVariables);
        this.ccEmails = ccEmails == null ? List.of() : List.copyOf(ccEmails);
        this.bccEmails = bccEmails == null ? List.of() : List.copyOf(bccEmails);
        this.attachments = attachments == null ? List.of() : List.copyOf(attachments);
        this.metadata = metadata == null ? Map.of() : Map.copyOf(metadata);

        // Default priority
        this.priority = priority == null ? NotificationPriority.NORMAL : priority;

        // Optional fields
        this.replyTo = replyTo;
        this.requestedBy = requestedBy;
    }

    /**
     * Builder constructor for easy creation.
     */
    public EmailNotificationRequestedEvent(
            String notificationId,
            String tenantId,
            String recipientEmail,
            String recipientName,
            String subject,
            String templateId,
            Map<String, Object> templateVariables,
            String requestedBy) {
        this(
                UUID.randomUUID().toString(),
                notificationId,
                tenantId,
                recipientEmail,
                recipientName,
                subject,
                templateId,
                templateVariables,
                NotificationPriority.NORMAL,
                List.of(),
                List.of(),
                List.of(),
                null,
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
     * Simple email validation.
     */
    private static boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
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
        private String tenantId = "default";
        private String recipientEmail;
        private String recipientName;
        private String subject;
        private String templateId;
        private Map<String, Object> templateVariables = Map.of();
        private NotificationPriority priority = NotificationPriority.NORMAL;
        private List<String> ccEmails = List.of();
        private List<String> bccEmails = List.of();
        private List<EmailAttachment> attachments = List.of();
        private String replyTo;
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

        public Builder recipientEmail(String recipientEmail) {
            this.recipientEmail = recipientEmail;
            return this;
        }

        public Builder recipientName(String recipientName) {
            this.recipientName = recipientName;
            return this;
        }

        public Builder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder templateId(String templateId) {
            this.templateId = templateId;
            return this;
        }

        public Builder templateVariables(Map<String, Object> templateVariables) {
            this.templateVariables = templateVariables;
            return this;
        }

        public Builder priority(NotificationPriority priority) {
            this.priority = priority;
            return this;
        }

        public Builder ccEmails(List<String> ccEmails) {
            this.ccEmails = ccEmails;
            return this;
        }

        public Builder bccEmails(List<String> bccEmails) {
            this.bccEmails = bccEmails;
            return this;
        }

        public Builder attachments(List<EmailAttachment> attachments) {
            this.attachments = attachments;
            return this;
        }

        public Builder replyTo(String replyTo) {
            this.replyTo = replyTo;
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

        public EmailNotificationRequestedEvent build() {
            return new EmailNotificationRequestedEvent(
                    eventId, notificationId, tenantId, recipientEmail, recipientName,
                    subject, templateId, templateVariables, priority, ccEmails, bccEmails,
                    attachments, replyTo, requestedBy, occurredAt, metadata
            );
        }
    }

    /**
     * Email attachment details.
     */
    public record EmailAttachment(
            @JsonProperty("fileName") String fileName,
            @JsonProperty("contentType") String contentType,
            @JsonProperty("size") long size,
            @JsonProperty("url") String url
    ) {
        @JsonCreator
        public EmailAttachment {
            if (fileName == null || fileName.isBlank()) {
                throw new IllegalArgumentException("fileName cannot be null or blank");
            }
            if (contentType == null || contentType.isBlank()) {
                throw new IllegalArgumentException("contentType cannot be null or blank");
            }
            if (size <= 0) {
                throw new IllegalArgumentException("size must be positive");
            }
        }
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