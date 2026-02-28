// ==========================================
// FILE: dto/SubscriptionResponse.java
// ==========================================
package com.svrmslk.tenant.presentation.dto;

import com.svrmslk.tenant.domain.model.Subscription;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionResponse {

    private Long id;
    private String tenantId;
    private Subscription.SubscriptionPlan plan;
    private Subscription.SubscriptionStatus status;
    private Subscription.BillingCycle billingCycle;
    private BigDecimal price;
    private String currency;
    private LocalDateTime trialEndDate;
    private LocalDateTime currentPeriodStart;
    private LocalDateTime currentPeriodEnd;
    private LocalDateTime nextBillingDate;
    private Boolean cancelAtPeriodEnd;
    private String paymentMethod;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}