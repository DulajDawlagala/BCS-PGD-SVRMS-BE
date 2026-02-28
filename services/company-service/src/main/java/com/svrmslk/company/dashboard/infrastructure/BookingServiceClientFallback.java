package com.svrmslk.company.dashboard.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

@Component
@Slf4j
public class BookingServiceClientFallback implements BookingServiceClient {

    @Override
    public BookingStats getBookingStatsByCompany(UUID companyId) {
        log.warn("Booking service unavailable, returning empty stats for company: {}", companyId);
        return BookingStats.builder()
                .activeRentals(0)
                .totalBookings(0)
                .monthlyRevenue(BigDecimal.ZERO)
                .totalRevenue(BigDecimal.ZERO)
                .utilizationRate(0.0)
                .recentBookings(Collections.emptyList())
                .build();
    }

    @Override
    public BookingStats getBookingStatsByVehicle(UUID vehicleId) {
        log.warn("Booking service unavailable, returning empty stats for vehicle: {}", vehicleId);
        return BookingStats.builder()
                .activeRentals(0)
                .totalBookings(0)
                .monthlyRevenue(BigDecimal.ZERO)
                .totalRevenue(BigDecimal.ZERO)
                .utilizationRate(0.0)
                .recentBookings(Collections.emptyList())
                .build();
    }
}