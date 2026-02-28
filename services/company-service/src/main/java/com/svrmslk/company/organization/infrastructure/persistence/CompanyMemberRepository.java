package com.svrmslk.company.organization.infrastructure.persistence;

import com.svrmslk.company.organization.domain.CompanyMember;
import com.svrmslk.company.shared.domain.CompanyId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CompanyMemberRepository {

    private final CompanyMemberJpaRepository jpaRepository;
    private final CompanyMemberEntityMapper mapper;

    public CompanyMember save(CompanyMember member) {
        CompanyMemberEntity entity = mapper.toEntity(member);
        CompanyMemberEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    public List<CompanyMember> findByCompanyId(CompanyId companyId) {
        return jpaRepository.findByCompanyId(companyId.value()).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    public List<CompanyMember> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}