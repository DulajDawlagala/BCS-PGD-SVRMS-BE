// FILE: dashboard/infrastructure/BookingStats.java
package com.svrmslk.customer.dashboard.infrastructure;

public record BookingStats(
        int totalBookings,
        int activeBookings,
        int completedBookings,
        int cancelledBookings
) {}