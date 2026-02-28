-- User sessions table
-- Tracks active user sessions with device and location information
CREATE TABLE user_sessions (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    session_token VARCHAR(255) NOT NULL UNIQUE,
    device_info VARCHAR(500),
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    last_accessed_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    terminated_at TIMESTAMP,
    CONSTRAINT fk_user_sessions_user_id FOREIGN KEY (user_id) REFERENCES user_accounts(id) ON DELETE CASCADE
);

-- Indexes for session management and cleanup
CREATE UNIQUE INDEX idx_user_sessions_session_token ON user_sessions(session_token);
CREATE INDEX idx_user_sessions_user_id_active ON user_sessions(user_id, active);
CREATE INDEX idx_user_sessions_expires_at ON user_sessions(expires_at);