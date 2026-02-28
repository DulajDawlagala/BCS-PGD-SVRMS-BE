package com.svrmslk.tenant.application.dto;

public record CreateTenantRequest(
        String name,
        String slug
) {}
