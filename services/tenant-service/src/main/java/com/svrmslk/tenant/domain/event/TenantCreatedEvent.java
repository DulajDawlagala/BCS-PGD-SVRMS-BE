// ==========================================
// FILE: event/TenantCreatedEvent.java
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
public class TenantCreatedEvent {
    private String tenantId;
    private String name;
    private String slug;
    private LocalDateTime timestamp;
}
