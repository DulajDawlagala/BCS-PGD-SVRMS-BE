package com.svrmslk.company.dashboard.presentation.mapper;

import com.svrmslk.company.dashboard.domain.CompanyDashboard;
import com.svrmslk.company.dashboard.domain.VehiclePerformance;
import com.svrmslk.company.dashboard.presentation.dto.DashboardResponse;
import com.svrmslk.company.dashboard.presentation.dto.VehiclePerformanceResponse;
import org.springframework.stereotype.Component;

@Component
public class DashboardDtoMapper {

    public DashboardResponse toResponse(CompanyDashboard dashboard) {
        return DashboardResponse.builder()
                .totalVehicles(dashboard.getTotalVehicles())
                .activeRentals(dashboard.getActiveRentals())
                .monthlyRevenue(dashboard.getMonthlyRevenue())
                .utilizationRatio(dashboard.getUtilizationRatio())
                .fleetStatusBreakdown(dashboard.getFleetStatusBreakdown())
                .recentBookings(dashboard.getRecentBookings())
                .build();
    }

    public VehiclePerformanceResponse toPerformanceResponse(VehiclePerformance performance) {
        return VehiclePerformanceResponse.builder()
                .vehicleId(performance.getVehicleId())
                .vehicleName(performance.getVehicleName())
                .totalBookings(performance.getTotalBookings())
                .totalRevenue(performance.getTotalRevenue())
                .utilizationRate(performance.getUtilizationRate())
                .build();
    }
}