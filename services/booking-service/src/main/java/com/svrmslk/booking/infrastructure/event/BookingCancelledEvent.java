package com.svrmslk.booking.infrastructure.event;

import com.svrmslk.booking.shared.event.DomainEvent;
import com.svrmslk.booking.shared.event.EventMetadata;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingCancelledEvent implements DomainEvent {
    private UUID bookingId;
    private UUID vehicleId;
    private UUID customerId;
    private UUID companyId;
    private UUID tenantId;
    private String reason;
    private Instant occurredAt;
    private EventMetadata metadata;

    @Override
    public String getEventType() {
        return "BOOKING_CANCELLED";
    }

    @Override
    public UUID getAggregateId() {
        return bookingId;
    }

    @Override
    public Instant getOccurredAt() {
        return occurredAt;
    }
}