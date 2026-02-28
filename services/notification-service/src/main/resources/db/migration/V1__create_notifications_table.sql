-- src/main/resources/db/migration/V1__create_notifications_table.sql
CREATE TABLE notifications (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(100) NOT NULL,
    event_id VARCHAR(255) NOT NULL,
    channel VARCHAR(50) NOT NULL,
    recipient VARCHAR(500) NOT NULL,
    subject VARCHAR(1000),
    content TEXT NOT NULL,
    status VARCHAR(50) NOT NULL,
    failure_reason VARCHAR(2000),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uk_tenant_event UNIQUE (tenant_id, event_id)
);

CREATE INDEX idx_tenant_id ON notifications(tenant_id);
CREATE INDEX idx_event_id ON notifications(event_id);
CREATE INDEX idx_status ON notifications(status);
CREATE INDEX idx_created_at ON notifications(created_at);

CREATE TABLE notification_metadata (
    notification_id UUID NOT NULL,
    meta_key VARCHAR(255) NOT NULL,
    meta_value VARCHAR(1000),
    PRIMARY KEY (notification_id, meta_key),
    CONSTRAINT fk_notification_metadata_notification
        FOREIGN KEY (notification_id)
        REFERENCES notifications(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_notification_metadata_notification_id ON notification_metadata(notification_id);

COMMENT ON TABLE notifications IS 'Stores notification records with multi-tenant isolation';
COMMENT ON COLUMN notifications.tenant_id IS 'Tenant identifier for multi-tenancy';
COMMENT ON COLUMN notifications.event_id IS 'Unique event identifier for idempotency';
COMMENT ON COLUMN notifications.channel IS 'Notification channel: EMAIL or IN_APP';
COMMENT ON COLUMN notifications.status IS 'Notification status: PENDING, SENT, or FAILED';
COMMENT ON CONSTRAINT uk_tenant_event ON notifications IS 'Ensures idempotency per tenant';