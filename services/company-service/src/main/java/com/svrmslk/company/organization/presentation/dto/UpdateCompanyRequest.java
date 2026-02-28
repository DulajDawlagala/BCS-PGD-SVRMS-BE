package com.svrmslk.company.organization.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompanyRequest {
    private String companyName;
    private String businessType;
    private String companyEmail;
    private String companyPhone;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String streetAddress;
    private String city;
    private String zipCode;
    private String paymentMethod;
}