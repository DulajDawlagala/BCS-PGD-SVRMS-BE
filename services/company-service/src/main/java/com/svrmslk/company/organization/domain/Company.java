package com.svrmslk.company.organization.domain;

import com.svrmslk.company.shared.domain.CompanyId;
import com.svrmslk.company.shared.domain.CompanyStatus;
import com.svrmslk.company.shared.domain.CompanyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Company {
    private CompanyId id;
    private UUID tenantId;
    private CompanyType type;
    private CompanyStatus status;

    // Company fields
    private String companyName;
    private String businessType;
    private String taxId;
    private String registrationNumber;
    private String companyEmail;
    private String companyPhone;

    // Owner fields
    private String firstName;
    private String lastName;
    private String email;
    private String phone;

    // Address
    private String streetAddress;
    private String city;
    private String zipCode;

    // Additional
    private String organizationSize;
    private String paymentMethod;
    private Integer maxVehicles;

    private Instant createdAt;
    private Instant updatedAt;

    public void validateIndividualLimits() {
        if (type == CompanyType.INDIVIDUAL && maxVehicles == null) {
            maxVehicles = 5;
        }
    }

    public boolean isIndividual() {
        return type == CompanyType.INDIVIDUAL;
    }
}