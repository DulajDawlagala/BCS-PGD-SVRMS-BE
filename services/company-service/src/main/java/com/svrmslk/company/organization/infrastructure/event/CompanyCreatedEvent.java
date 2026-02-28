package com.svrmslk.company.organization.infrastructure.event;

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
public class CompanyCreatedEvent implements DomainEvent {
    private UUID companyId;
    private UUID tenantId;
    private String type;
    private String companyName;
    private String email;
    private Instant occurredAt;
    private EventMetadata metadata;

    @Override
    public String getEventType() {
        return "COMPANY_CREATED";
    }

    @Override
    public UUID getAggregateId() {
        return companyId;
    }

    @Override
    public Instant getOccurredAt() {
        return occurredAt;
    }
}