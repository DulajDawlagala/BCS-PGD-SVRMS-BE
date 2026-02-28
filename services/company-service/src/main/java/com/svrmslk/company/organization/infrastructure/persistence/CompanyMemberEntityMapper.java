package com.svrmslk.company.organization.infrastructure.persistence;

import com.svrmslk.company.organization.domain.CompanyMember;
import com.svrmslk.company.shared.domain.CompanyId;
import org.springframework.stereotype.Component;

@Component
public class CompanyMemberEntityMapper {

    public CompanyMemberEntity toEntity(CompanyMember member) {
        return CompanyMemberEntity.builder()
                .id(member.getId())
                .companyId(member.getCompanyId().value())
                .userId(member.getUserId())
                .role(member.getRole())
                .joinedAt(member.getJoinedAt())
                .tenantId(member.getTenantId())
                .build();
    }

    public CompanyMember toDomain(CompanyMemberEntity entity) {
        return CompanyMember.builder()
                .id(entity.getId())
                .companyId(new CompanyId(entity.getCompanyId()))
                .userId(entity.getUserId())
                .role(entity.getRole())
                .joinedAt(entity.getJoinedAt())
                .tenantId(entity.getTenantId())
                .build();
    }
}