package com.svrmslk.company.organization.presentation.dto;

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
public class CompanyMemberResponse {
    private UUID id;
    private UUID companyId;
    private UUID userId;
    private MemberRole role;
    private Instant joinedAt;
}