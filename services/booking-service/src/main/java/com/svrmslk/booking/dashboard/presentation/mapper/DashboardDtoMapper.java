package com.svrmslk.booking.dashboard.presentation.mapper;

import com.svrmslk.booking.dashboard.domain.CompanyDashboard;
import com.svrmslk.booking.dashboard.domain.CustomerDashboard;
import com.svrmslk.booking.dashboard.domain.VehiclePerformance;
import com.svrmslk.booking.dashboard.presentation.dto.CompanyDashboardResponse;
import com.svrmslk.booking.dashboard.presentation.dto.CustomerDashboardResponse;
import com.svrmslk.booking.dashboard.presentation.dto.VehiclePerformanceResponse;
import org.springframework.stereotype.Component;

@Component
public class DashboardDtoMapper {

    public CustomerDashboardResponse toCustomerResponse(CustomerDashboard dashboard) {
        return CustomerDashboardResponse.builder()
                .totalBookings(dashboard.getTotalBookings())
                .activeBookings(dashboard.getActiveBookings())
                .completedBookings(dashboard.getCompletedBookings())
                .totalSpent(dashboard.getTotalSpent())
                .build();
    }

    public CompanyDashboardResponse toCompanyResponse(CompanyDashboard dashboard) {
        return CompanyDashboardResponse.builder()
                .totalBookings(dashboard.getTotalBookings())
                .activeBookings(dashboard.getActiveBookings())
                .monthlyRevenue(dashboard.getMonthlyRevenue())
                .totalRevenue(dashboard.getTotalRevenue())
                .build();
    }

    public VehiclePerformanceResponse toVehiclePerformanceResponse(VehiclePerformance performance) {
        return VehiclePerformanceResponse.builder()
                .vehicleId(performance.getVehicleId())
                .totalBookings(performance.getTotalBookings())
                .totalRevenue(performance.getTotalRevenue())
                .utilizationRate(performance.getUtilizationRate())
                .build();
    }
}