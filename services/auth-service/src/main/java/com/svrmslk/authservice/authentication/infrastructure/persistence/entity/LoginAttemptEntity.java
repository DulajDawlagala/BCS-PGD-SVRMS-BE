package com.svrmslk.authservice.authentication.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "login_attempts", indexes = {
        @Index(name = "idx_user_id_timestamp", columnList = "user_id,attempt_timestamp"),
        @Index(name = "idx_ip_address_timestamp", columnList = "ip_address,attempt_timestamp")
})
public class LoginAttemptEntity {

    @Id
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(nullable = false)
    private boolean successful;

    @Column(name = "failure_reason", length = 255)
    private String failureReason;

    @Column(name = "attempt_timestamp", nullable = false)
    private Instant attemptTimestamp;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public Instant getAttemptTimestamp() {
        return attemptTimestamp;
    }

    public void setAttemptTimestamp(Instant attemptTimestamp) {
        this.attemptTimestamp = attemptTimestamp;
    }
}