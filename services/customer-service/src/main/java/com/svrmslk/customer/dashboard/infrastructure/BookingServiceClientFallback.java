// FILE: dashboard/infrastructure/BookingServiceClientFallback.java
package com.svrmslk.customer.dashboard.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BookingServiceClientFallback implements BookingServiceClient {

    @Override
    public BookingStats getBookingStats(String customerId) {
        log.warn("Fallback triggered for booking stats, customerId: {}", customerId);
        return new BookingStats(0, 0, 0, 0);
    }
}