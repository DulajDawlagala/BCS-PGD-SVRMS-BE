-- External identities table
-- Links OAuth2 provider accounts to internal user accounts
CREATE TABLE external_identities (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    provider VARCHAR(50) NOT NULL,
    provider_user_id VARCHAR(255) NOT NULL,
    provider_email VARCHAR(255),
    provider_name VARCHAR(255),
    access_token_hash VARCHAR(500),
    refresh_token_hash VARCHAR(500),
    token_expires_at TIMESTAMP,
    linked_at TIMESTAMP NOT NULL,
    last_used_at TIMESTAMP,
    CONSTRAINT fk_external_identities_user_id FOREIGN KEY (user_id) REFERENCES user_accounts(id) ON DELETE CASCADE,
    CONSTRAINT uq_external_identities_provider_user UNIQUE (provider, provider_user_id)
);

-- Indexes for OAuth lookup
CREATE UNIQUE INDEX idx_external_identities_provider_user_id ON external_identities(provider, provider_user_id);
CREATE INDEX idx_external_identities_user_id ON external_identities(user_id);