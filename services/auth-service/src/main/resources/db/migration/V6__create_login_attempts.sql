-- Login attempts table
-- Tracks all login attempts for security monitoring and rate limiting
CREATE TABLE login_attempts (
    id UUID PRIMARY KEY,
    user_id UUID,
    email VARCHAR(255) NOT NULL,
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    successful BOOLEAN NOT NULL,
    failure_reason VARCHAR(255),
    attempt_timestamp TIMESTAMP NOT NULL,
    CONSTRAINT fk_login_attempts_user_id FOREIGN KEY (user_id) REFERENCES user_accounts(id) ON DELETE SET NULL
);

-- Indexes for security analysis and cleanup
CREATE INDEX idx_login_attempts_user_id_timestamp ON login_attempts(user_id, attempt_timestamp);
CREATE INDEX idx_login_attempts_ip_address_timestamp ON login_attempts(ip_address, attempt_timestamp);
CREATE INDEX idx_login_attempts_email ON login_attempts(email);
CREATE INDEX idx_login_attempts_timestamp ON login_attempts(attempt_timestamp);