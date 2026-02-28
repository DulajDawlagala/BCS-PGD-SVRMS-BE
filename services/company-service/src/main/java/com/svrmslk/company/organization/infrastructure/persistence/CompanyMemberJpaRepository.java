package com.svrmslk.company.organization.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CompanyMemberJpaRepository extends JpaRepository<CompanyMemberEntity, UUID> {
    List<CompanyMemberEntity> findByCompanyId(UUID companyId);
    List<CompanyMemberEntity> findByUserId(UUID userId);
}