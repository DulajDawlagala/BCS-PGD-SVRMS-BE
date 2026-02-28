-- OTP tokens table
-- Stores hashed one-time passwords for multi-factor authentication
CREATE TABLE otp_tokens (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    otp_hash VARCHAR(255) NOT NULL,
    otp_type VARCHAR(50) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    verified_at TIMESTAMP,
    failed_attempts INTEGER NOT NULL DEFAULT 0,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_otp_tokens_user_id FOREIGN KEY (user_id) REFERENCES user_accounts(id) ON DELETE CASCADE
);

-- Indexes for OTP verification and cleanup
CREATE INDEX idx_otp_tokens_user_id_type ON otp_tokens(user_id, otp_type);
CREATE INDEX idx_otp_tokens_expires_at ON otp_tokens(expires_at);