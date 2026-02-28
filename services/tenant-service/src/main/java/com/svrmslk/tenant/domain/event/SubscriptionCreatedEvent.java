// ==========================================
// FILE: event/SubscriptionCreatedEvent.java
// ==========================================
package com.svrmslk.tenant.domain.event;

import com.svrmslk.tenant.domain.model.Subscription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionCreatedEvent {
    private String tenantId;
    private Subscription.SubscriptionPlan plan;
    private Subscription.BillingCycle billingCycle;
    private BigDecimal price;
    private LocalDateTime timestamp;
}
