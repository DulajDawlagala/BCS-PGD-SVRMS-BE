-- ==========================================
-- V2__create_tenants_table.sql
-- Tenant Service - Tenants
-- PostgreSQL 16
-- ==========================================

CREATE TABLE tenants (
    id BIGSERIAL PRIMARY KEY,

    tenant_id VARCHAR(36) NOT NULL UNIQUE,

    name VARCHAR(100) NOT NULL,
    slug VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(500),

    company_name VARCHAR(200),
    company_email VARCHAR(100),
    company_phone VARCHAR(20),
    company_address VARCHAR(500),
    company_city VARCHAR(100),
    company_country VARCHAR(100),
    company_postal_code VARCHAR(20),

    status VARCHAR(20) NOT NULL,

    database_schema VARCHAR(100),

    max_users INTEGER,
    max_branches INTEGER,
    max_vehicles INTEGER,
    storage_quota_gb INTEGER,

    custom_domain VARCHAR(255),
    logo_url VARCHAR(500),

    primary_color VARCHAR(7),
    secondary_color VARCHAR(7),

    activated_at TIMESTAMP,
    deactivated_at TIMESTAMP,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- =========================
-- ENUM SAFETY (CHECK)
-- =========================

ALTER TABLE tenants
ADD CONSTRAINT chk_tenant_status
CHECK (status IN (
    'PENDING',
    'ACTIVE',
    'SUSPENDED',
    'INACTIVE',
    'CANCELLED'
));

-- =========================
-- INDEXES
-- =========================

CREATE INDEX idx_tenant_slug
    ON tenants (slug);

CREATE INDEX idx_tenant_status
    ON tenants (status);
