package com.svrmslk.company.organization.infrastructure.persistence;

import com.svrmslk.company.shared.domain.CompanyStatus;
import com.svrmslk.company.shared.domain.CompanyType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "companies")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CompanyEntity {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "tenant_id", nullable = false, columnDefinition = "UUID")
    private UUID tenantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CompanyType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CompanyStatus status;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "business_type")
    private String businessType;

    @Column(name = "tax_id")
    private String taxId;

    @Column(name = "registration_number")
    private String registrationNumber;

    @Column(name = "company_email")
    private String companyEmail;

    @Column(name = "company_phone")
    private String companyPhone;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "street_address")
    private String streetAddress;

    @Column(name = "city")
    private String city;

    @Column(name = "zip_code")
    private String zipCode;

    @Column(name = "organization_size")
    private String organizationSize;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "max_vehicles")
    private Integer maxVehicles;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}