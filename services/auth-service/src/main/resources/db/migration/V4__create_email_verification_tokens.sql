-- Email verification tokens table
-- Stores hashed verification tokens for email confirmation
CREATE TABLE email_verification_tokens (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    token_hash VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    verified_at TIMESTAMP,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_email_verification_tokens_user_id FOREIGN KEY (user_id) REFERENCES user_accounts(id) ON DELETE CASCADE
);

-- Indexes for token verification and cleanup
CREATE UNIQUE INDEX idx_email_verification_tokens_token_hash ON email_verification_tokens(token_hash);
CREATE INDEX idx_email_verification_tokens_user_id ON email_verification_tokens(user_id);
CREATE INDEX idx_email_verification_tokens_expires_at ON email_verification_tokens(expires_at);