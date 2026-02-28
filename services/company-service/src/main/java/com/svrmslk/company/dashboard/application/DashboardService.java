package com.svrmslk.company.dashboard.application;

import com.svrmslk.company.dashboard.domain.CompanyDashboard;
import com.svrmslk.company.dashboard.domain.RecentBooking;
import com.svrmslk.company.dashboard.domain.VehiclePerformance;
import com.svrmslk.company.dashboard.infrastructure.BookingServiceClient;
import com.svrmslk.company.dashboard.infrastructure.BookingStats;
import com.svrmslk.company.organization.domain.Company;
import com.svrmslk.company.organization.infrastructure.persistence.CompanyRepository;
import com.svrmslk.company.shared.domain.CompanyId;
import com.svrmslk.company.shared.exception.CompanyNotFoundException;
import com.svrmslk.company.vehicle.domain.Vehicle;
import com.svrmslk.company.vehicle.domain.VehicleStatus;
import com.svrmslk.company.vehicle.infrastructure.persistence.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final CompanyRepository companyRepository;
    private final VehicleRepository vehicleRepository;
    private final BookingServiceClient bookingServiceClient;

    public CompanyDashboard getCompanyDashboard(UUID companyId) {
        Company company = companyRepository.findById(new CompanyId(companyId))
                .orElseThrow(() -> new CompanyNotFoundException(companyId));

        List<Vehicle> vehicles = vehicleRepository.findByCompanyId(company.getId());

        BookingStats bookingStats = bookingServiceClient.getBookingStatsByCompany(companyId);

        Map<String, Integer> fleetStatus = vehicles.stream()
                .collect(Collectors.groupingBy(
                        v -> v.getStatus().name(),
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));

        double utilizationRatio = vehicles.isEmpty() ? 0.0 :
                (double) fleetStatus.getOrDefault(VehicleStatus.RENTED.name(), 0) / vehicles.size();

        return CompanyDashboard.builder()
                .totalVehicles(vehicles.size())
                .activeRentals(bookingStats.getActiveRentals())
                .monthlyRevenue(bookingStats.getMonthlyRevenue())
                .utilizationRatio(utilizationRatio)
                .fleetStatusBreakdown(fleetStatus)
                .recentBookings(mapRecentBookings(bookingStats.getRecentBookings()))
                .build();
    }

    public List<VehiclePerformance> getVehiclePerformance(UUID companyId) {
        Company company = companyRepository.findById(new CompanyId(companyId))
                .orElseThrow(() -> new CompanyNotFoundException(companyId));

        List<Vehicle> vehicles = vehicleRepository.findByCompanyId(company.getId());

        return vehicles.stream()
                .map(v -> {
                    BookingStats vehicleStats = bookingServiceClient.getBookingStatsByVehicle(v.getId().value());
                    return VehiclePerformance.builder()
                            .vehicleId(v.getId().value())
                            .vehicleName(v.getMake() + " " + v.getModel())
                            .totalBookings(vehicleStats.getTotalBookings())
                            .totalRevenue(vehicleStats.getTotalRevenue())
                            .utilizationRate(vehicleStats.getUtilizationRate())
                            .build();
                })
                .collect(Collectors.toList());
    }

    private List<RecentBooking> mapRecentBookings(List<Map<String, Object>> rawBookings) {
        if (rawBookings == null) return Collections.emptyList();

        return rawBookings.stream()
                .map(booking -> RecentBooking.builder()
                        .bookingId(UUID.fromString((String) booking.get("bookingId")))
                        .vehicleName((String) booking.get("vehicleName"))
                        .customerName((String) booking.get("customerName"))
                        .startDate((Instant) booking.get("startDate"))
                        .endDate((Instant) booking.get("endDate"))
                        .totalAmount(new BigDecimal(booking.get("totalAmount").toString()))
                        .status((String) booking.get("status"))
                        .build())
                .collect(Collectors.toList());
    }
}