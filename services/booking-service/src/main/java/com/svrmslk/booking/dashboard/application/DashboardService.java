package com.svrmslk.booking.dashboard.application;

import com.svrmslk.booking.dashboard.domain.CompanyDashboard;
import com.svrmslk.booking.dashboard.domain.CustomerDashboard;
import com.svrmslk.booking.dashboard.domain.VehiclePerformance;
import com.svrmslk.booking.dashboard.infrastructure.AnalyticsRepository;
import com.svrmslk.booking.domain.Booking;
import com.svrmslk.booking.infrastructure.persistence.BookingRepository;
import com.svrmslk.booking.shared.domain.CompanyId;
import com.svrmslk.booking.shared.domain.CustomerId;
import com.svrmslk.booking.shared.security.BookingSecurityContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final BookingRepository bookingRepository;
    private final AnalyticsRepository analyticsRepository;
    private final BookingSecurityContext securityContext;

//    public CustomerDashboard getCustomerDashboard() {
//        UUID customerId = securityContext.getUserId();
//        UUID tenantId = securityContext.getTenantId();
//
//        List<Booking> bookings = bookingRepository.findByCustomerIdAndTenantId(
//                CustomerId.of(customerId),
//                tenantId
//        );
//
//        long totalBookings = analyticsRepository.countTotalBookingsByCustomer(customerId, tenantId);
//        long activeBookings = bookings.stream().filter(Booking::isActive).count();
//        long completedBookings = totalBookings - activeBookings;
//        BigDecimal totalSpent = analyticsRepository.calculateTotalSpentByCustomer(customerId, tenantId);
//
//        return CustomerDashboard.builder()
//                .totalBookings((int) totalBookings)
//                .activeBookings((int) activeBookings)
//                .completedBookings((int) completedBookings)
//                .totalSpent(totalSpent)
//                .build();
//    }
// Update the method signature
// Inside DashboardService.java

    public CustomerDashboard getCustomerDashboard(UUID customerId, UUID tenantId) {
        // 1. Fetch bookings using the tenant-less method (since customers are global)
        List<Booking> bookings = bookingRepository.findByCustomerId(CustomerId.of(customerId));

        // 2. Fetch total count from analytics (Calling the new 1-argument method)
        long totalBookings = analyticsRepository.countTotalBookingsByCustomer(customerId);

        long activeBookings = bookings.stream()
                .filter(Booking::isActive)
                .count();

        long completedBookings = totalBookings - activeBookings;

        // 3. Fetch total spent (Calling the new 1-argument method)
        BigDecimal totalSpent = analyticsRepository.calculateTotalSpentByCustomer(customerId);
        if (totalSpent == null) totalSpent = BigDecimal.ZERO;

        return CustomerDashboard.builder()
                .totalBookings((int) totalBookings)
                .activeBookings((int) activeBookings)
                .completedBookings((int) completedBookings)
                .totalSpent(totalSpent)
                .build();
    }
    public CompanyDashboard getCompanyDashboard(UUID companyId) {
        UUID tenantId = securityContext.getTenantId();

        List<Booking> bookings = bookingRepository.findByCompanyIdAndTenantId(
                CompanyId.of(companyId),
                tenantId
        );

        long activeBookings = analyticsRepository.countActiveBookingsByCompany(companyId, tenantId);
        BigDecimal monthlyRevenue = analyticsRepository.calculateMonthlyRevenue(companyId, tenantId);
        BigDecimal totalRevenue = bookings.stream()
                .map(Booking::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CompanyDashboard.builder()
                .totalBookings(bookings.size())
                .activeBookings((int) activeBookings)
                .monthlyRevenue(monthlyRevenue)
                .totalRevenue(totalRevenue)
                .build();
    }

    public List<VehiclePerformance> getVehiclePerformance(UUID companyId) {
        UUID tenantId = securityContext.getTenantId();
        return analyticsRepository.getVehiclePerformance(companyId, tenantId);
    }
}