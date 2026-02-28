CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE companies (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    company_name VARCHAR(255),
    business_type VARCHAR(100),
    tax_id VARCHAR(100),
    registration_number VARCHAR(100),
    company_email VARCHAR(255),
    company_phone VARCHAR(50),
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    email VARCHAR(255),
    phone VARCHAR(50),
    street_address VARCHAR(255),
    city VARCHAR(100),
    zip_code VARCHAR(20),
    organization_size VARCHAR(50),
    payment_method VARCHAR(100),
    max_vehicles INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_companies_tenant_id ON companies(tenant_id);
CREATE INDEX idx_companies_type ON companies(type);
CREATE INDEX idx_companies_status ON companies(status);