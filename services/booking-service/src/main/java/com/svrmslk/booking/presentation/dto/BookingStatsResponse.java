package com.svrmslk.booking.presentation.dto;

public record BookingStatsResponse(
        int totalBookings,
        int activeBookings,
        int completedBookings,
        int cancelledBookings
) {}