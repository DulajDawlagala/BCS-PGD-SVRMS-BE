// ==========================================
// FILE: dto/SubscriptionRequest.java
// ==========================================
package com.svrmslk.tenant.presentation.dto;

import com.svrmslk.tenant.domain.model.Subscription;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionRequest {

    @NotNull(message = "Tenant ID is required")
    private String tenantId;

    @NotNull(message = "Subscription plan is required")
    private Subscription.SubscriptionPlan plan;

    @NotNull(message = "Billing cycle is required")
    private Subscription.BillingCycle billingCycle;

    private String paymentMethod;
    private String stripeCustomerId;
}
