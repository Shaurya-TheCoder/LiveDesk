ALTER TABLE agents
    ADD COLUMN max_concurrency INTEGER NOT NULL DEFAULT 3;

ALTER TABLE agents
    ADD COLUMN is_online BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE agents
    ADD COLUMN active_chat_count INTEGER NOT NULL DEFAULT 0;

CREATE INDEX idx_tickets_status_created_at
    ON tickets(status, created_at);