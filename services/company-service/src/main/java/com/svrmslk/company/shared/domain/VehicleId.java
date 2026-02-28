package com.svrmslk.company.shared.domain;

import java.util.UUID;

public record VehicleId(UUID value) {
    public static VehicleId generate() {
        return new VehicleId(UUID.randomUUID());
    }

    public static VehicleId of(String value) {
        return new VehicleId(UUID.fromString(value));
    }
}