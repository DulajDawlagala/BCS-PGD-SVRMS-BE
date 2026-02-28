package com.svrmslk.company.organization.infrastructure.persistence;

import com.svrmslk.company.organization.domain.Company;
import com.svrmslk.company.shared.domain.CompanyId;
import org.springframework.stereotype.Component;

@Component
public class CompanyEntityMapper {

    public CompanyEntity toEntity(Company company) {
        return CompanyEntity.builder()
                .id(company.getId().value())
                .tenantId(company.getTenantId())
                .type(company.getType())
                .status(company.getStatus())
                .companyName(company.getCompanyName())
                .businessType(company.getBusinessType())
                .taxId(company.getTaxId())
                .registrationNumber(company.getRegistrationNumber())
                .companyEmail(company.getCompanyEmail())
                .companyPhone(company.getCompanyPhone())
                .firstName(company.getFirstName())
                .lastName(company.getLastName())
                .email(company.getEmail())
                .phone(company.getPhone())
                .streetAddress(company.getStreetAddress())
                .city(company.getCity())
                .zipCode(company.getZipCode())
                .organizationSize(company.getOrganizationSize())
                .paymentMethod(company.getPaymentMethod())
                .maxVehicles(company.getMaxVehicles())
                .createdAt(company.getCreatedAt())
                .updatedAt(company.getUpdatedAt())
                .build();
    }

    public Company toDomain(CompanyEntity entity) {
        return Company.builder()
                .id(new CompanyId(entity.getId()))
                .tenantId(entity.getTenantId())
                .type(entity.getType())
                .status(entity.getStatus())
                .companyName(entity.getCompanyName())
                .businessType(entity.getBusinessType())
                .taxId(entity.getTaxId())
                .registrationNumber(entity.getRegistrationNumber())
                .companyEmail(entity.getCompanyEmail())
                .companyPhone(entity.getCompanyPhone())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .streetAddress(entity.getStreetAddress())
                .city(entity.getCity())
                .zipCode(entity.getZipCode())
                .organizationSize(entity.getOrganizationSize())
                .paymentMethod(entity.getPaymentMethod())
                .maxVehicles(entity.getMaxVehicles())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}