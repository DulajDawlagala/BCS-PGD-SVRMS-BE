// ==========================================
// FILE: dto/TenantRequest.java
// ==========================================
package com.svrmslk.tenant.presentation.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantRequest {

    @NotBlank(message = "Tenant name is required")
    @Size(min = 2, max = 100, message = "Tenant name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Slug is required")
    @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug must contain only lowercase letters, numbers, and hyphens")
    @Size(min = 3, max = 50, message = "Slug must be between 3 and 50 characters")
    private String slug;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotBlank(message = "Company name is required")
    @Size(max = 200, message = "Company name must not exceed 200 characters")
    private String companyName;

    @NotBlank(message = "Company email is required")
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
    private String primaryColor;
    private String secondaryColor;
}
