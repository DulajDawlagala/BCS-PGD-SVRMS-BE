// ==========================================
// FILE: event/TenantActivatedEvent.java
// ==========================================
package com.svrmslk.tenant.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantActivatedEvent {
    private String tenantId;
    private String name;
    private LocalDateTime activatedAt;
    private LocalDateTime timestamp;
}