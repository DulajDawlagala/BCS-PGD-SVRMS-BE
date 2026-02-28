package com.svrmslk.company.dashboard.infrastructure;

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
public class BookingStats {
    private int activeRentals;
    private int totalBookings;
    private BigDecimal monthlyRevenue;
    private BigDecimal totalRevenue;
    private double utilizationRate;
    private List<Map<String, Object>> recentBookings;
}