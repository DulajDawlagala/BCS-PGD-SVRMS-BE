package com.svrmslk.company.shared.domain;

import java.util.UUID;

public record CompanyId(UUID value) {
    public static CompanyId generate() {
        return new CompanyId(UUID.randomUUID());
    }

    public static CompanyId of(String value) {
        return new CompanyId(UUID.fromString(value));
    }
}