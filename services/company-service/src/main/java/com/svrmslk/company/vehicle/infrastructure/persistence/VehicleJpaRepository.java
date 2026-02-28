package com.svrmslk.company.vehicle.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.svrmslk.company.vehicle.domain.VehicleStatus;

import java.util.List;
import java.util.UUID;

@Repository
public interface VehicleJpaRepository extends JpaRepository<VehicleEntity, UUID> {
    List<VehicleEntity> findByCompanyId(UUID companyId);
    List<VehicleEntity> findByStatus(VehicleStatus status);
    long countByCompanyId(UUID companyId);
}