
// ==========================================
// FILE: entity/Tenant.java
// ==========================================
package com.svrmslk.tenant.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity
@Table(name = "tenants", indexes = {
        @Index(name = "idx_tenant_slug", columnList = "slug", unique = true),
        @Index(name = "idx_tenant_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false, unique = true, length = 36)
    private String tenantId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 50)
    private String slug;

    @Column(length = 500)
    private String description;

    @Column(name = "company_name", length = 200)
    private String companyName;

    @Column(name = "company_email", length = 100)
    private String companyEmail;

    @Column(name = "company_phone", length = 20)
    private String companyPhone;

    @Column(name = "company_address", length = 500)
    private String companyAddress;

    @Column(name = "company_city", length = 100)
    private String companyCity;

    @Column(name = "company_country", length = 100)
    private String companyCountry;

    @Column(name = "company_postal_code", length = 20)
    private String companyPostalCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TenantStatus status;

    @Column(name = "database_schema", length = 100)
    private String databaseSchema;

    @Column(name = "max_users")
    private Integer maxUsers;

    @Column(name = "max_branches")
    private Integer maxBranches;

    @Column(name = "max_vehicles")
    private Integer maxVehicles;

    @Column(name = "storage_quota_gb")
    private Integer storageQuotaGb;

    @Column(name = "custom_domain", length = 255)
    private String customDomain;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "primary_color", length = 7)
    private String primaryColor;

    @Column(name = "secondary_color", length = 7)
    private String secondaryColor;

    @Column(name = "activated_at")
    private LocalDateTime activatedAt;

    @Column(name = "deactivated_at")
    private LocalDateTime deactivatedAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = TenantStatus.PENDING;
        }
        if (this.tenantId == null) {
            this.tenantId = java.util.UUID.randomUUID().toString();
        }
    }

    public enum TenantStatus {
        PENDING,
        ACTIVE,
        SUSPENDED,
        INACTIVE,
        CANCELLED
    }
}