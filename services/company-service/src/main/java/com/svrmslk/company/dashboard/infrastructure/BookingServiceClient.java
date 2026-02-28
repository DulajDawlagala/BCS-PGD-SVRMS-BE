package com.svrmslk.company.dashboard.infrastructure;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "booking-service", url = "${services.booking.url}", fallback = BookingServiceClientFallback.class)
public interface BookingServiceClient {

    @GetMapping("/api/v1/bookings/stats/company/{companyId}")
    BookingStats getBookingStatsByCompany(@PathVariable UUID companyId);

    @GetMapping("/api/v1/bookings/stats/vehicle/{vehicleId}")
    BookingStats getBookingStatsByVehicle(@PathVariable UUID vehicleId);
}