// FILE: dashboard/application/DashboardService.java
package com.svrmslk.customer.dashboard.application;

import com.svrmslk.customer.dashboard.domain.CustomerDashboard;
import com.svrmslk.customer.dashboard.infrastructure.BookingServiceClient;
import com.svrmslk.customer.dashboard.infrastructure.BookingStats;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final BookingServiceClient bookingServiceClient;

    public CustomerDashboard getDashboard(String customerId) {
        log.info("Fetching dashboard for customer: {}", customerId);

        try {
            BookingStats stats = bookingServiceClient.getBookingStats(customerId);

            return new CustomerDashboard(
                    customerId,
                    stats.totalBookings(),
                    stats.activeBookings(),
                    stats.completedBookings(),
                    stats.cancelledBookings()
            );
        } catch (Exception e) {
            log.error("Failed to fetch booking stats for customer: {}", customerId, e);
            // Return empty dashboard on failure
            return new CustomerDashboard(customerId, 0, 0, 0, 0);
        }
    }
}
