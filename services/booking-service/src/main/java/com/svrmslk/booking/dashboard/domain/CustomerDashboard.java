package com.svrmslk.booking.dashboard.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDashboard {
    private int totalBookings;
    private int activeBookings;
    private int completedBookings;
    private BigDecimal totalSpent;
}