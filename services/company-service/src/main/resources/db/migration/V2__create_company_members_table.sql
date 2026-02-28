CREATE TABLE company_members (
    id UUID PRIMARY KEY,
    company_id UUID NOT NULL,
    user_id UUID NOT NULL,
    role VARCHAR(50) NOT NULL,
    joined_at TIMESTAMP NOT NULL,
    tenant_id UUID NOT NULL,
    CONSTRAINT fk_company_members_company FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE
);

CREATE INDEX idx_company_members_company_id ON company_members(company_id);
CREATE INDEX idx_company_members_user_id ON company_members(user_id);
CREATE UNIQUE INDEX idx_company_members_company_user ON company_members(company_id, user_id);