package com.svrmslk.booking.shared.domain;

import java.util.UUID;

public record VehicleId(UUID value) {
    public static VehicleId of(UUID value) {
        return new VehicleId(value);
    }

    public static VehicleId of(String value) {
        return new VehicleId(UUID.fromString(value));
    }
}