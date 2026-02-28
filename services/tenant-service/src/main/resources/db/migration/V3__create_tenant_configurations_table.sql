-- ==========================================
-- V3__create_tenant_configurations_table.sql
-- Tenant Service - Tenant Configurations
-- PostgreSQL 16
-- ==========================================

CREATE TABLE tenant_configurations (
    id BIGSERIAL PRIMARY KEY,

    tenant_id VARCHAR(36) NOT NULL,

    config_key VARCHAR(100) NOT NULL,
    config_value TEXT,
    config_type VARCHAR(50),
    description VARCHAR(500),

    is_encrypted BOOLEAN,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- =========================
-- INDEXES
-- =========================

CREATE INDEX idx_config_tenant_id
    ON tenant_configurations (tenant_id);

CREATE INDEX idx_config_key
    ON tenant_configurations (config_key);

-- =========================
-- OPTIONAL (STRONGLY RECOMMENDED)
-- Prevent duplicate keys per tenant
-- =========================

CREATE UNIQUE INDEX ux_tenant_config_unique_key
    ON tenant_configurations (tenant_id, config_key);
