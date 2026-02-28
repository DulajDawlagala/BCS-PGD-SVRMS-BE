package com.svrmslk.company.organization.presentation.dto;

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
public class CompanyResponse {
    private UUID id;
    private UUID tenantId;
    private CompanyType type;
    private CompanyStatus status;
    private String companyName;
    private String businessType;
    private String taxId;
    private String registrationNumber;
    private String companyEmail;
    private String companyPhone;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String streetAddress;
    private String city;
    private String zipCode;
    private String organizationSize;
    private String paymentMethod;
    private Integer maxVehicles;
    private Instant createdAt;
    private Instant updatedAt;
}