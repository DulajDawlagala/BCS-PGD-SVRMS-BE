package com.svrmslk.booking.shared.domain;

import java.util.UUID;

public record CompanyId(UUID value) {
    public static CompanyId of(UUID value) {
        return new CompanyId(value);
    }

    public static CompanyId of(String value) {
        return new CompanyId(UUID.fromString(value));
    }
}