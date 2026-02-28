package com.svrmslk.company.internal;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class TenantContextResponse {

    private UUID tenantId;
    private String tenantType;
    private String effectiveRole;

    public static TenantContextResponse empty() {
        return TenantContextResponse.builder()
                .tenantId(null)
                .tenantType(null)
                .effectiveRole(null)
                .build();
    }
}
