package com.svrmslk.company.organization.infrastructure.persistence;

import com.svrmslk.company.organization.domain.Invitation;
import com.svrmslk.company.shared.domain.CompanyId;
import com.svrmslk.company.shared.domain.Email;
import org.springframework.stereotype.Component;

@Component
public class InvitationEntityMapper {

    public InvitationEntity toEntity(Invitation invitation) {
        return InvitationEntity.builder()
                .id(invitation.getId())
                .companyId(invitation.getCompanyId().value())
                .email(invitation.getEmail().value())
                .role(invitation.getRole())
                .token(invitation.getToken())
                .expiresAt(invitation.getExpiresAt())
                .accepted(invitation.isAccepted())
                .createdAt(invitation.getCreatedAt())
                .tenantId(invitation.getTenantId())
                .build();
    }

    public Invitation toDomain(InvitationEntity entity) {
        return Invitation.builder()
                .id(entity.getId())
                .companyId(new CompanyId(entity.getCompanyId()))
                .email(new Email(entity.getEmail()))
                .role(entity.getRole())
                .token(entity.getToken())
                .expiresAt(entity.getExpiresAt())
                .accepted(entity.isAccepted())
                .createdAt(entity.getCreatedAt())
                .tenantId(entity.getTenantId())
                .build();
    }
}