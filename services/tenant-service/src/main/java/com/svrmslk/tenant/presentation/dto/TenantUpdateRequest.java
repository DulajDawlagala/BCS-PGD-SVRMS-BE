// ==========================================
// FILE: dto/TenantUpdateRequest.java
// ==========================================
package com.svrmslk.tenant.presentation.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantUpdateRequest {

    @Size(min = 2, max = 100, message = "Tenant name must be between 2 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Email(message = "Invalid email format")
    private String companyEmail;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String companyPhone;

    private String companyAddress;
    private String companyCity;
    private String companyCountry;
    private String companyPostalCode;

    private Integer maxUsers;
    private Integer maxBranches;
    private Integer maxVehicles;
    private Integer storageQuotaGb;

    private String customDomain;
    private String logoUrl;
    private String primaryColor;
    private String secondaryColor;
}
