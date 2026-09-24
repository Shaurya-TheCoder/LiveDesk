DROP INDEX IF EXISTS idx_agents_online_active_chat;

ALTER TABLE agents
DROP COLUMN IF EXISTS is_online;