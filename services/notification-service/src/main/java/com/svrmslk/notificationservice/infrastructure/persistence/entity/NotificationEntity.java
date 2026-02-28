// com/svrmslk/notificationservice/infrastructure/persistence/entity/NotificationEntity.java
package com.svrmslk.notificationservice.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(
        name = "notifications",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_tenant_event", columnNames = {"tenant_id", "event_id"})
        },
        indexes = {
                @Index(name = "idx_tenant_id", columnList = "tenant_id"),
                @Index(name = "idx_event_id", columnList = "event_id"),
                @Index(name = "idx_status", columnList = "status"),
                @Index(name = "idx_created_at", columnList = "created_at")
        }
)
public class NotificationEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "tenant_id", nullable = false, length = 100)
    private String tenantId;

    @Column(name = "event_id", nullable = false, length = 255)
    private String eventId;

    @Column(name = "channel", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private ChannelType channel;

    @Column(name = "recipient", nullable = false, length = 500)
    private String recipient;

    @Column(name = "subject", length = 1000)
    private String subject;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "notification_metadata",
            joinColumns = @JoinColumn(name = "notification_id"),
            indexes = @Index(name = "idx_notification_metadata_notification_id", columnList = "notification_id")
    )
    @MapKeyColumn(name = "meta_key", length = 255)
    @Column(name = "meta_value", length = 1000)
    private Map<String, String> metadata = new HashMap<>();

    @Column(name = "status", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private StatusType status;

    @Column(name = "failure_reason", length = 2000)
    private String failureReason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public enum ChannelType {
        EMAIL,
        IN_APP
    }

    public enum StatusType {
        PENDING,
        SENT,
        FAILED
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (updatedAt == null) {
            updatedAt = createdAt;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public ChannelType getChannel() {
        return channel;
    }

    public void setChannel(ChannelType channel) {
        this.channel = channel;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public StatusType getStatus() {
        return status;
    }

    public void setStatus(StatusType status) {
        this.status = status;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}