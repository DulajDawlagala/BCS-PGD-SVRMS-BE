package com.svrmslk.company.organization.presentation.dto;

import com.svrmslk.company.shared.domain.CompanyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCompanyRequest {
    private CompanyType type;
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
}