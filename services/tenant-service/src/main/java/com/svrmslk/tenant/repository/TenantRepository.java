// ==========================================
// FILE: repository/TenantRepository.java
// ==========================================
package com.svrmslk.tenant.repository;

import com.svrmslk.tenant.domain.model.Tenant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {

    Optional<Tenant> findByTenantId(String tenantId);

    Optional<Tenant> findBySlug(String slug);

    boolean existsBySlug(String slug);

    boolean existsByTenantId(String tenantId);

    Page<Tenant> findByStatus(Tenant.TenantStatus status, Pageable pageable);

    @Query("SELECT t FROM Tenant t WHERE " +
            "LOWER(t.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(t.slug) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(t.companyName) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Tenant> searchTenants(@Param("search") String search, Pageable pageable);

    @Query("SELECT COUNT(t) FROM Tenant t WHERE t.status = :status")
    long countByStatus(@Param("status") Tenant.TenantStatus status);
}
