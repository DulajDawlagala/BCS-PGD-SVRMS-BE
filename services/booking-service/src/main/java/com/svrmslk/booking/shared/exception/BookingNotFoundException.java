package com.svrmslk.booking.shared.exception;

import java.util.UUID;

public class BookingNotFoundException extends DomainException {
    public BookingNotFoundException(UUID bookingId) {
        super("Booking not found: " + bookingId);
    }
}