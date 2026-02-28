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
public class InvitationResponse {
    private UUID id;
    private UUID companyId;
    private String email;
    private MemberRole role;
    private String token;
    private Instant expiresAt;
    private boolean accepted;
    private Instant createdAt;
}