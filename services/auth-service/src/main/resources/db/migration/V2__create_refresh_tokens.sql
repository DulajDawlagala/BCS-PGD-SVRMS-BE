-- Refresh tokens table
-- Stores JWT refresh tokens with rotation support
CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    token VARCHAR(500) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_refresh_tokens_user_id FOREIGN KEY (user_id) REFERENCES user_accounts(id) ON DELETE CASCADE
);

-- Indexes for token lookup and cleanup
CREATE UNIQUE INDEX idx_refresh_tokens_token ON refresh_tokens(token);
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_expires_at ON refresh_tokens(expires_at);