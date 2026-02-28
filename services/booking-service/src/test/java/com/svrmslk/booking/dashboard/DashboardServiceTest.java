package com.svrmslk.booking.dashboard;

import com.svrmslk.booking.dashboard.application.DashboardService;
import com.svrmslk.booking.dashboard.domain.CustomerDashboard;
import com.svrmslk.booking.dashboard.infrastructure.AnalyticsRepository;
import com.svrmslk.booking.domain.Booking;
import com.svrmslk.booking.domain.BookingStatus;
import com.svrmslk.booking.infrastructure.persistence.BookingRepository;
import com.svrmslk.booking.shared.domain.BookingId;
import com.svrmslk.booking.shared.domain.CompanyId;
import com.svrmslk.booking.shared.domain.CustomerId;
import com.svrmslk.booking.shared.domain.VehicleId;
import com.svrmslk.booking.shared.security.BookingSecurityContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private AnalyticsRepository analyticsRepository;

    @Mock
    private BookingSecurityContext securityContext;

    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        dashboardService = new DashboardService(bookingRepository, analyticsRepository, securityContext);
    }

    @Test
    void getCustomerDashboard_shouldReturnCorrectMetrics() {
        UUID customerId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        when(securityContext.getUserId()).thenReturn(customerId);
        when(securityContext.getTenantId()).thenReturn(tenantId);

        Booking activeBooking = Booking.builder()
                .id(BookingId.generate())
                .customerId(CustomerId.of(customerId))
                .vehicleId(VehicleId.of(UUID.randomUUID()))
                .companyId(CompanyId.of(UUID.randomUUID()))
                .status(BookingStatus.CONFIRMED)
                .price(new BigDecimal("100.00"))
                .build();

        when(bookingRepository.findByCustomerIdAndTenantId(any(CustomerId.class), eq(tenantId)))
                .thenReturn(Arrays.asList(activeBooking));
        when(analyticsRepository.countTotalBookingsByCustomer(customerId, tenantId)).thenReturn(5L);
        when(analyticsRepository.calculateTotalSpentByCustomer(customerId, tenantId))
                .thenReturn(new BigDecimal("500.00"));

       // CustomerDashboard dashboard = dashboardService.getCustomerDashboard();
// Act - UPDATE THIS LINE 👇
        CustomerDashboard dashboard = dashboardService.getCustomerDashboard(customerId, tenantId);
        assertEquals(5, dashboard.getTotalBookings());
        assertEquals(1, dashboard.getActiveBookings());
        assertEquals(new BigDecimal("500.00"), dashboard.getTotalSpent());
    }
}
