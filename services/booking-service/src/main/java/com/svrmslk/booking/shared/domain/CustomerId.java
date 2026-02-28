package com.svrmslk.booking.shared.domain;

import java.util.UUID;

public record CustomerId(UUID value) {
    public static CustomerId of(UUID value) {
        return new CustomerId(value);
    }

    public static CustomerId of(String value) {
        return new CustomerId(UUID.fromString(value));
    }
}