ALTER TABLE agents
    ADD COLUMN max_concurrency INTEGER NOT NULL DEFAULT 3;

ALTER TABLE agents
    ADD COLUMN is_online BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE agents
    ADD COLUMN active_chat_count INTEGER NOT NULL DEFAULT 0;

CREATE INDEX idx_messages_sent_at
    ON messages(sent_at);

CREATE INDEX idx_agents_online_active_chat
    ON agents(is_online, active_chat_count);