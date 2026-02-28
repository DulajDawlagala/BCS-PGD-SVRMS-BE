// ==========================================
// FILE: dto/TenantResponse.java
// ==========================================
package com.svrmslk.tenant.presentation.dto;

import com.svrmslk.tenant.domain.model.Tenant;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantResponse {

    private Long id;
    private String tenantId;
    private String name;
    private String slug;
    private String description;
    private String companyName;
    private String companyEmail;
    private String companyPhone;
    private String companyAddress;
    private String companyCity;
    private String companyCountry;
    private String companyPostalCode;
    private Tenant.TenantStatus status;
    private String databaseSchema;
    private Integer maxUsers;
    private Integer maxBranches;
    private Integer maxVehicles;
    private Integer storageQuotaGb;
    private String customDomain;
    private String logoUrl;
    private String primaryColor;
    private String secondaryColor;
    private LocalDateTime activatedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
