package com.svrmslk.company.organization.domain;

import com.svrmslk.company.shared.domain.CompanyId;
import com.svrmslk.company.shared.domain.Email;
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
public class Invitation {
    private UUID id;
    private CompanyId companyId;
    private Email email;
    private MemberRole role;
    private String token;
    private Instant expiresAt;
    private boolean accepted;
    private Instant createdAt;
    private UUID tenantId;

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}