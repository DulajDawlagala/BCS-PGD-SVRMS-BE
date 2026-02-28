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
@Table(name = "company_members")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyMemberEntity {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "company_id", nullable = false, columnDefinition = "UUID")
    private UUID companyId;

    @Column(name = "user_id", nullable = false, columnDefinition = "UUID")
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole role;

    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt;

    @Column(name = "tenant_id", nullable = false, columnDefinition = "UUID")
    private UUID tenantId;
}