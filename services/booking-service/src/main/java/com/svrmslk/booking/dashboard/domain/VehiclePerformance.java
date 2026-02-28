package com.svrmslk.booking.dashboard.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehiclePerformance {
    private UUID vehicleId;
    private int totalBookings;
    private BigDecimal totalRevenue;
    private double utilizationRate;
}