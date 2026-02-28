CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE bookings (
    id UUID PRIMARY KEY,
    vehicle_id UUID NOT NULL,
    customer_id UUID NOT NULL,
    company_id UUID NOT NULL,
    tenant_id UUID NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_bookings_customer_id ON bookings(customer_id);
CREATE INDEX idx_bookings_company_id ON bookings(company_id);
CREATE INDEX idx_bookings_vehicle_id ON bookings(vehicle_id);
CREATE INDEX idx_bookings_tenant_id ON bookings(tenant_id);
CREATE INDEX idx_bookings_status ON bookings(status);
CREATE INDEX idx_bookings_start_time ON bookings(start_time);
CREATE INDEX idx_bookings_customer_tenant ON bookings(customer_id, tenant_id);
CREATE INDEX idx_bookings_company_tenant ON bookings(company_id, tenant_id);