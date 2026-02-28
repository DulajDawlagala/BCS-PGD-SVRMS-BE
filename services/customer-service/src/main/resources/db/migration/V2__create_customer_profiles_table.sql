CREATE TABLE IF NOT EXISTS customer_profiles (
    id BIGSERIAL PRIMARY KEY,
    customer_id VARCHAR(36) NOT NULL UNIQUE,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    phone_number VARCHAR(20),
    date_of_birth DATE,
    nationality VARCHAR(50),
    drivers_license_number VARCHAR(50),
    license_expiry_date DATE,
    email_notifications BOOLEAN DEFAULT TRUE,
    sms_notifications BOOLEAN DEFAULT TRUE,
    marketing_emails BOOLEAN DEFAULT FALSE,
    preferred_language VARCHAR(10) DEFAULT 'en',
    preferred_currency VARCHAR(3) DEFAULT 'USD',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_profile_customer FOREIGN KEY (customer_id)
        REFERENCES customers(customer_id) ON DELETE CASCADE
);

CREATE INDEX idx_profile_customer_id ON customer_profiles(customer_id);
CREATE INDEX idx_profile_license_expiry ON customer_profiles(license_expiry_date);

COMMENT ON TABLE customer_profiles IS 'Extended customer profile information';
COMMENT ON COLUMN customer_profiles.drivers_license_number IS 'Driver license number for vehicle rental';
