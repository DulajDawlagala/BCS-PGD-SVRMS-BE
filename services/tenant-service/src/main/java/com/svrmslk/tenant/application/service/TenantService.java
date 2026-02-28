package com.svrmslk.tenant.application.service;

import com.svrmslk.tenant.domain.model.Tenant;
import com.svrmslk.tenant.repository.TenantRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TenantService {

    private final TenantRepository tenantRepository;

    public TenantService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    public List<Tenant> findAll() {
        return tenantRepository.findAll();
    }

    public Tenant findById(Long id) {
        return tenantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));
    }

    public Tenant save(Tenant tenant) {
        return tenantRepository.save(tenant);
    }
}
