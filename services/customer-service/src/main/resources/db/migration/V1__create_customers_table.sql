CREATE TABLE IF NOT EXISTS customers (
    customer_id VARCHAR(36) PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    registered_at TIMESTAMP NOT NULL,
    last_login_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    auth_user_id VARCHAR(36),
    CONSTRAINT chk_customer_status CHECK (status IN ('ACTIVE', 'SUSPENDED'))
);

CREATE INDEX idx_customer_email ON customers(email);
CREATE INDEX idx_customer_status ON customers(status);
CREATE INDEX idx_customer_registered_at ON customers(registered_at);

COMMENT ON TABLE customers IS 'Core customer accounts';
COMMENT ON COLUMN customers.customer_id IS 'Unique customer identifier (UUID)';
COMMENT ON COLUMN customers.email IS 'Customer email address (unique)';
COMMENT ON COLUMN customers.status IS 'Customer account status';
