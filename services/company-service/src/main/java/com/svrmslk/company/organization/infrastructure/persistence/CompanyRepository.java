package com.svrmslk.company.organization.infrastructure.persistence;

import com.svrmslk.company.organization.domain.Company;
import com.svrmslk.company.shared.domain.CompanyId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CompanyRepository {

    private final CompanyJpaRepository jpaRepository;
    private final CompanyEntityMapper mapper;

    public Company save(Company company) {
        CompanyEntity entity = mapper.toEntity(company);
        CompanyEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    public Optional<Company> findById(CompanyId id) {
        return jpaRepository.findById(id.value())
                .map(mapper::toDomain);
    }

    public List<Company> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    public List<Company> findByTenantId(UUID tenantId) {
        return jpaRepository.findByTenantId(tenantId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}