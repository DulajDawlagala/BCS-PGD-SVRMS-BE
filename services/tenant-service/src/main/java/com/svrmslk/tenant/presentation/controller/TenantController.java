package com.svrmslk.tenant.presentation.controller;

import com.svrmslk.tenant.application.service.TenantService;
import com.svrmslk.tenant.domain.model.Tenant;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tenants")
public class TenantController {

    private final TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

//    @PostMapping
//    public Tenant create(@RequestBody CreateTenantRequest request) {
//        return tenantService.create(request);
//    }

    @GetMapping
    public List<Tenant> getAll() {
        return tenantService.findAll();
    }

    @GetMapping("/{id}")
    public Tenant getById(@PathVariable Long id) {
        return tenantService.findById(id);
    }
}
