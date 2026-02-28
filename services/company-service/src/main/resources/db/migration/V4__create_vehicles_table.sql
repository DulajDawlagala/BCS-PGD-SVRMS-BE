CREATE TABLE vehicles (
    id UUID PRIMARY KEY,
    company_id UUID NOT NULL,
    tenant_id UUID NOT NULL,
    make VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    year INTEGER NOT NULL,
    color VARCHAR(50),
    license_plate VARCHAR(50) UNIQUE,
    vin VARCHAR(100) UNIQUE,
    image_urls JSONB,
    registration_doc_url VARCHAR(500),
    insurance_doc_url VARCHAR(500),
    hourly_rate DECIMAL(10,2),
    daily_rate DECIMAL(10,2),
    weekly_rate DECIMAL(10,2),
    monthly_rate DECIMAL(10,2),
    available_days JSONB,
    min_rental_hours INTEGER,
    max_rental_days INTEGER,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_vehicles_company FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE
);

CREATE INDEX idx_vehicles_company_id ON vehicles(company_id);
CREATE INDEX idx_vehicles_tenant_id ON vehicles(tenant_id);
CREATE INDEX idx_vehicles_status ON vehicles(status);
CREATE INDEX idx_vehicles_license_plate ON vehicles(license_plate);