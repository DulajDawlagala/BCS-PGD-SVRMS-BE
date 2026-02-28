package com.svrmslk.booking.domain;

import com.svrmslk.booking.shared.domain.BookingId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookingTest {

    @Test
    void cancel_shouldUpdateStatus_whenBookingIsPending() {
        Booking booking = Booking.builder()
                .id(BookingId.generate())
                .status(BookingStatus.PENDING)
                .build();

        booking.cancel();

        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
    }

    @Test
    void cancel_shouldThrowException_whenBookingIsAlreadyCancelled() {
        Booking booking = Booking.builder()
                .id(BookingId.generate())
                .status(BookingStatus.CANCELLED)
                .build();

        assertThrows(IllegalStateException.class, booking::cancel);
    }

    @Test
    void confirm_shouldUpdateStatus_whenBookingIsPending() {
        Booking booking = Booking.builder()
                .id(BookingId.generate())
                .status(BookingStatus.PENDING)
                .build();

        booking.confirm();

        assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
    }

    @Test
    void isActive_shouldReturnTrue_whenStatusIsConfirmed() {
        Booking booking = Booking.builder()
                .status(BookingStatus.CONFIRMED)
                .build();

        assertTrue(booking.isActive());
    }

    @Test
    void isActive_shouldReturnFalse_whenStatusIsCancelled() {
        Booking booking = Booking.builder()
                .status(BookingStatus.CANCELLED)
                .build();

        assertFalse(booking.isActive());
    }
}