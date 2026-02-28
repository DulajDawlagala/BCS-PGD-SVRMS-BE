// FILE: dashboard/infrastructure/BookingServiceClient.java
package com.svrmslk.customer.dashboard.infrastructure;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "booking-service", url = "${services.booking-service.url}",
        fallback = BookingServiceClientFallback.class)
public interface BookingServiceClient {

    @GetMapping("/api/v1/bookings/customer/{customerId}/stats")
    BookingStats getBookingStats(@PathVariable String customerId);
}