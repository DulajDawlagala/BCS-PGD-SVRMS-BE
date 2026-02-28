package com.svrmslk.company.dashboard.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDashboard {
    private int totalVehicles;
    private int activeRentals;
    private BigDecimal monthlyRevenue;
    private double utilizationRatio;
    private Map<String, Integer> fleetStatusBreakdown;
    private List<RecentBooking> recentBookings;
}