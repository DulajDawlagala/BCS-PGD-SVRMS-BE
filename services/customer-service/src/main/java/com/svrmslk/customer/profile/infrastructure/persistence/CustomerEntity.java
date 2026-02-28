// FILE: profile/infrastructure/persistence/CustomerEntity.java
package com.svrmslk.customer.profile.infrastructure.persistence;

import com.svrmslk.customer.shared.domain.CustomerStatus;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity
@Table(name = "customers", indexes = {
        @Index(name = "idx_customer_email", columnList = "email", unique = true),
        @Index(name = "idx_customer_status", columnList = "status")
})
@EntityListeners(AuditingEntityListener.class)
public class CustomerEntity {

    @Id
    @Column(name = "customer_id", length = 36)
    private String customerId;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CustomerStatus status;

    @Column(name = "registered_at", nullable = false)
    private LocalDateTime registeredAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "auth_user_id", nullable = false, unique = true, length = 36)
    private String authUserId;

    // Getters and Setters
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public CustomerStatus getStatus() { return status; }
    public void setStatus(CustomerStatus status) { this.status = status; }

    public LocalDateTime getRegisteredAt() { return registeredAt; }
    public void setRegisteredAt(LocalDateTime registeredAt) { this.registeredAt = registeredAt; }

    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public String getAuthUserId() { return authUserId; }
    public void setAuthUserId(String authUserId) { this.authUserId = authUserId; }
}
