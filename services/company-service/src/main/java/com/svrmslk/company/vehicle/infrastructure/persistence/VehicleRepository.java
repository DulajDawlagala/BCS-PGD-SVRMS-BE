package com.svrmslk.company.vehicle.infrastructure.persistence;

import com.svrmslk.company.shared.domain.CompanyId;
import com.svrmslk.company.shared.domain.VehicleId;
import com.svrmslk.company.vehicle.domain.Vehicle;
import com.svrmslk.company.vehicle.domain.VehicleStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class VehicleRepository {

    private final VehicleJpaRepository jpaRepository;
    private final VehicleEntityMapper mapper;

    public Vehicle save(Vehicle vehicle) {
        VehicleEntity entity = mapper.toEntity(vehicle);
        VehicleEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    public Optional<Vehicle> findById(VehicleId id) {
        return jpaRepository.findById(id.value())
                .map(mapper::toDomain);
    }

    public List<Vehicle> findByCompanyId(CompanyId companyId) {
        return jpaRepository.findByCompanyId(companyId.value()).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    public List<Vehicle> findByStatus(VehicleStatus status) {
        return jpaRepository.findByStatus(status).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    public long countByCompanyId(CompanyId companyId) {
        return jpaRepository.countByCompanyId(companyId.value());
    }

    public void delete(VehicleId id) {
        jpaRepository.deleteById(id.value());
    }
}