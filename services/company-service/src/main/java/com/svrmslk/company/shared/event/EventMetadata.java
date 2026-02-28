package com.svrmslk.company.shared.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventMetadata {
    private UUID eventId;
    private String version;
    private String source;
    private UUID tenantId;
    private UUID userId;
}