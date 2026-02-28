// ==========================================
// FILE: entity/TenantConfiguration.java
// ==========================================
package com.svrmslk.tenant.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity
@Table(name = "tenant_configurations", indexes = {
        @Index(name = "idx_config_tenant_id", columnList = "tenant_id"),
        @Index(name = "idx_config_key", columnList = "config_key")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class TenantConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false, length = 36)
    private String tenantId;

    @Column(name = "config_key", nullable = false, length = 100)
    private String configKey;

    @Column(name = "config_value", columnDefinition = "TEXT")
    private String configValue;

    @Column(name = "config_type", length = 50)
    private String configType;

    @Column(length = 500)
    private String description;

    @Column(name = "is_encrypted")
    private Boolean isEncrypted;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public static final String EMAIL_ENABLED = "email.enabled";
    public static final String SMS_ENABLED = "sms.enabled";
    public static final String BOOKING_AUTO_CONFIRM = "booking.auto_confirm";
    public static final String BOOKING_BUFFER_MINUTES = "booking.buffer_minutes";
    public static final String PAYMENT_CURRENCY = "payment.currency";
    public static final String TIMEZONE = "system.timezone";
    public static final String DATE_FORMAT = "system.date_format";
    public static final String LANGUAGE = "system.language";
}
