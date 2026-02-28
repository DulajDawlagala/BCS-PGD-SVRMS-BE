package com.svrmslk.booking.domain;

import com.svrmslk.booking.shared.domain.BookingId;
import com.svrmslk.booking.shared.domain.CompanyId;
import com.svrmslk.booking.shared.domain.CustomerId;
import com.svrmslk.booking.shared.domain.VehicleId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    private BookingId id;
    private VehicleId vehicleId;
    private CustomerId customerId;
    private CompanyId companyId;
    private UUID tenantId;

    private Instant startTime;
    private Instant endTime;
    private BookingStatus status;
    private BigDecimal price;

    private Instant createdAt;
    private Instant updatedAt;

    public void cancel() {
        if (this.status == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking is already cancelled");
        }
        if (this.status == BookingStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel completed booking");
        }
        this.status = BookingStatus.CANCELLED;
        this.updatedAt = Instant.now();
    }

    public void confirm() {
        if (this.status != BookingStatus.PENDING) {
            throw new IllegalStateException("Only pending bookings can be confirmed");
        }
        this.status = BookingStatus.CONFIRMED;
        this.updatedAt = Instant.now();
    }

    public boolean isActive() {
        return status == BookingStatus.CONFIRMED || status == BookingStatus.PENDING;
    }
}