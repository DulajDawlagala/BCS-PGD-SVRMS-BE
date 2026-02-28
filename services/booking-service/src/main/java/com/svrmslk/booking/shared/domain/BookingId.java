package com.svrmslk.booking.shared.domain;

import java.util.UUID;

public record BookingId(UUID value) {
    public static BookingId generate() {
        return new BookingId(UUID.randomUUID());
    }

    public static BookingId of(String value) {
        return new BookingId(UUID.fromString(value));
    }
}