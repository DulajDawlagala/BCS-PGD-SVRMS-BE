-- ==========================================
-- V0__init_triggers.sql
-- Common trigger utilities
-- PostgreSQL 16
-- ==========================================

-- =========================
-- updated_at trigger function
-- =========================

CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- =========================
-- Optional: created_at safeguard
-- (prevents accidental overwrite)
-- =========================

CREATE OR REPLACE FUNCTION protect_created_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.created_at = OLD.created_at;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
