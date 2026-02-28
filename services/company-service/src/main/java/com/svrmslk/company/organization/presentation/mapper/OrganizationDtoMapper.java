package com.svrmslk.company.organization.presentation.mapper;

import com.svrmslk.company.organization.application.command.CreateCompanyCommand;
import com.svrmslk.company.organization.application.command.UpdateCompanyCommand;
import com.svrmslk.company.organization.domain.Company;
import com.svrmslk.company.organization.domain.CompanyMember;
import com.svrmslk.company.organization.domain.Invitation;
import com.svrmslk.company.organization.presentation.dto.*;
import org.springframework.stereotype.Component;

@Component
public class OrganizationDtoMapper {

    public CreateCompanyCommand toCreateCommand(CreateCompanyRequest request) {
        return CreateCompanyCommand.builder()
                .type(request.getType())
                .companyName(request.getCompanyName())
                .businessType(request.getBusinessType())
                .taxId(request.getTaxId())
                .registrationNumber(request.getRegistrationNumber())
                .companyEmail(request.getCompanyEmail())
                .companyPhone(request.getCompanyPhone())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .streetAddress(request.getStreetAddress())
                .city(request.getCity())
                .zipCode(request.getZipCode())
                .organizationSize(request.getOrganizationSize())
                .paymentMethod(request.getPaymentMethod())
                .build();
    }

    public UpdateCompanyCommand toUpdateCommand(UpdateCompanyRequest request) {
        return UpdateCompanyCommand.builder()
                .companyName(request.getCompanyName())
                .businessType(request.getBusinessType())
                .companyEmail(request.getCompanyEmail())
                .companyPhone(request.getCompanyPhone())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .streetAddress(request.getStreetAddress())
                .city(request.getCity())
                .zipCode(request.getZipCode())
                .paymentMethod(request.getPaymentMethod())
                .build();
    }

    public CompanyResponse toResponse(Company company) {
        return CompanyResponse.builder()
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

    public InvitationResponse toInvitationResponse(Invitation invitation) {
        return InvitationResponse.builder()
                .id(invitation.getId())
                .companyId(invitation.getCompanyId().value())
                .email(invitation.getEmail().value())
                .role(invitation.getRole())
                .token(invitation.getToken())
                .expiresAt(invitation.getExpiresAt())
                .accepted(invitation.isAccepted())
                .createdAt(invitation.getCreatedAt())
                .build();
    }

    public CompanyMemberResponse toMemberResponse(CompanyMember member) {
        return CompanyMemberResponse.builder()
                .id(member.getId())
                .companyId(member.getCompanyId().value())
                .userId(member.getUserId())
                .role(member.getRole())
                .joinedAt(member.getJoinedAt())
                .build();
    }
}