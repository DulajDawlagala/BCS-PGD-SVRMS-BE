package com.svrmslk.company.organization.infrastructure.persistence;

import com.svrmslk.company.shared.domain.MemberRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "invitations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitationEntity {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "company_id", nullable = false, columnDefinition = "UUID")
    private UUID companyId;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole role;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean accepted;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "tenant_id", nullable = false, columnDefinition = "UUID")
    private UUID tenantId;
}