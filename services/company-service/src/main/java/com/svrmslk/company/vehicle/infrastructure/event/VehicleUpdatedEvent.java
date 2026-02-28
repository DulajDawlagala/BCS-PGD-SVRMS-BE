package com.svrmslk.company.vehicle.infrastructure.event;

import com.svrmslk.company.shared.event.DomainEvent;
import com.svrmslk.company.shared.event.EventMetadata;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleUpdatedEvent implements DomainEvent {
    private UUID vehicleId;
    private UUID companyId;
    private String status;
    private Instant occurredAt;
    private EventMetadata metadata;

    @Override
    public String getEventType() {
        return "VEHICLE_UPDATED";
    }

    @Override
    public UUID getAggregateId() {
        return vehicleId;
    }

    @Override
    public Instant getOccurredAt() {
        return occurredAt;
    }
}