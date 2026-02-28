-- User accounts table
-- Stores core authentication data for all users
CREATE TABLE user_accounts (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    failed_login_attempts INTEGER NOT NULL DEFAULT 0,
    locked_until TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    session_id VARCHAR(255),
    CONSTRAINT chk_status CHECK (status IN ('ACTIVE', 'LOCKED', 'DISABLED'))
);

-- Indexes for performance
CREATE INDEX idx_user_accounts_email ON user_accounts(email);
CREATE INDEX idx_user_accounts_session_id ON user_accounts(session_id);
CREATE INDEX idx_user_accounts_status ON user_accounts(status);

-- User roles table (many-to-many relationship)
CREATE TABLE user_roles (
    user_id UUID NOT NULL,
    role VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, role),
    CONSTRAINT fk_user_roles_user_id FOREIGN KEY (user_id) REFERENCES user_accounts(id) ON DELETE CASCADE,
    CONSTRAINT chk_role CHECK (role IN ('CUSTOMER', 'PROVIDER', 'SYSTEM_ADMIN', 'COMPANY_ADMIN', 'OWNER'))
);

CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);