package com.svrmslk.authservice.authentication.domain.model;

public enum UserRole {
    CUSTOMER,
    PROVIDER,
    SYSTEM_ADMIN,
    COMPANY_ADMIN,
    OWNER;

    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}