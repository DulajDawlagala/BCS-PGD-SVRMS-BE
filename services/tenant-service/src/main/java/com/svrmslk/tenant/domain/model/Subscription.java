
// ==========================================
// FILE: entity/Subscription.java
// ==================
package com.svrmslk.tenant.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "subscriptions", indexes = {
        @Index(name = "idx_subscription_tenant_id", columnList = "tenant_id"),
        @Index(name = "idx_subscription_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false, length = 36)
    private String tenantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SubscriptionPlan plan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SubscriptionStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "billing_cycle", nullable = false, length = 20)
    private BillingCycle billingCycle;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(length = 3)
    private String currency;

    @Column(name = "trial_end_date")
    private LocalDateTime trialEndDate;

    @Column(name = "current_period_start", nullable = false)
    private LocalDateTime currentPeriodStart;

    @Column(name = "current_period_end", nullable = false)
    private LocalDateTime currentPeriodEnd;

    @Column(name = "next_billing_date")
    private LocalDateTime nextBillingDate;

    @Column(name = "cancel_at_period_end")
    private Boolean cancelAtPeriodEnd;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(name = "stripe_subscription_id", length = 100)
    private String stripeSubscriptionId;

    @Column(name = "stripe_customer_id", length = 100)
    private String stripeCustomerId;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum SubscriptionPlan {
        FREE,
        STARTER,
        PROFESSIONAL,
        ENTERPRISE
    }

    public enum SubscriptionStatus {
        TRIAL,
        ACTIVE,
        PAST_DUE,
        CANCELLED,
        EXPIRED
    }

    public enum BillingCycle {
        MONTHLY,
        QUARTERLY,
        YEARLY
    }

    public boolean isActive() {
        return status == SubscriptionStatus.ACTIVE || status == SubscriptionStatus.TRIAL;
    }

    public boolean isExpired() {
        return currentPeriodEnd != null && currentPeriodEnd.isBefore(LocalDateTime.now());
    }
}
