package com.svrmslk.company.organization.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CompanyJpaRepository extends JpaRepository<CompanyEntity, UUID> {

    @Query("SELECT c FROM CompanyEntity c WHERE c.id IN " +
            "(SELECT cm.companyId FROM CompanyMemberEntity cm WHERE cm.userId = :userId)")
    List<CompanyEntity> findByUserId(@Param("userId") UUID userId);

    List<CompanyEntity> findByTenantId(UUID tenantId);
}