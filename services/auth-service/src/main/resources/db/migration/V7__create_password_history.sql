-- Password history table
-- Prevents password reuse by storing historical password hashes
CREATE TABLE password_history (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_password_history_user_id FOREIGN KEY (user_id) REFERENCES user_accounts(id) ON DELETE CASCADE
);

-- Indexes for password reuse checking
CREATE INDEX idx_password_history_user_id_created ON password_history(user_id, created_at DESC);