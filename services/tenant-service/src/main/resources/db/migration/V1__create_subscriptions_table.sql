-- ==========================================
-- V1__create_subscriptions_table.sql
-- Tenant Service - Subscriptions
-- PostgreSQL 16
-- ==========================================

CREATE TABLE subscriptions (
    id BIGSERIAL PRIMARY KEY,

    tenant_id VARCHAR(36) NOT NULL,

    plan VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    billing_cycle VARCHAR(20) NOT NULL,

    price NUMERIC(10, 2) NOT NULL,
    currency VARCHAR(3),

    trial_end_date TIMESTAMP,
    current_period_start TIMESTAMP NOT NULL,
    current_period_end TIMESTAMP NOT NULL,
    next_billing_date TIMESTAMP,

    cancel_at_period_end BOOLEAN,
    cancelled_at TIMESTAMP,

    payment_method VARCHAR(50),
    stripe_subscription_id VARCHAR(100),
    stripe_customer_id VARCHAR(100),

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- =========================
-- ENUM SAFETY (CHECKS)
-- =========================

ALTER TABLE subscriptions
ADD CONSTRAINT chk_subscription_plan
CHECK (plan IN ('FREE', 'STARTER', 'PROFESSIONAL', 'ENTERPRISE'));

ALTER TABLE subscriptions
ADD CONSTRAINT chk_subscription_status
CHECK (status IN ('TRIAL', 'ACTIVE', 'PAST_DUE', 'CANCELLED', 'EXPIRED'));

ALTER TABLE subscriptions
ADD CONSTRAINT chk_billing_cycle
CHECK (billing_cycle IN ('MONTHLY', 'QUARTERLY', 'YEARLY'));

-- =========================
-- INDEXES
-- =========================

CREATE INDEX idx_subscription_tenant_id
    ON subscriptions (tenant_id);

CREATE INDEX idx_subscription_status
    ON subscriptions (status);
