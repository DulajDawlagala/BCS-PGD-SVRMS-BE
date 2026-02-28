package com.svrmslk.company.organization.domain;

import com.svrmslk.company.shared.domain.CompanyId;
import com.svrmslk.company.shared.domain.MemberRole;
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
public class CompanyMember {
    private UUID id;
    private CompanyId companyId;
    private UUID userId;
    private MemberRole role;
    private Instant joinedAt;
    private UUID tenantId;
}