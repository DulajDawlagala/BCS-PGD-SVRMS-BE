package com.svrmslk.booking.dashboard.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDashboardResponse {
    private int totalBookings;
    private int activeBookings;
    private BigDecimal monthlyRevenue;
    private BigDecimal totalRevenue;
}