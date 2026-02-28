// FILE: dashboard/presentation/dto/DashboardResponse.java
package com.svrmslk.customer.dashboard.presentation.dto;

public record DashboardResponse(
        String customerId,
        int totalBookings,
        int activeBookings,
        int completedBookings,
        int cancelledBookings
) {}