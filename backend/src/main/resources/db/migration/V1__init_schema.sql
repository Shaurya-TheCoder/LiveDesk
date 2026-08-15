CREATE TABLE agents (
                        id UUID NOT NULL PRIMARY KEY,
                        email VARCHAR(255) NOT NULL UNIQUE,
                        password_hash VARCHAR(255) NOT NULL,
                        role VARCHAR(20) NOT NULL
);

CREATE TABLE tickets (
                         id UUID NOT NULL PRIMARY KEY,
                         status VARCHAR(20) NOT NULL,
                         created_at TIMESTAMP NOT NULL,
                         assigned_agent_id UUID,

                         CONSTRAINT fk_tickets_assigned_agent
                             FOREIGN KEY (assigned_agent_id)
                                 REFERENCES agents(id)
);

CREATE TABLE messages (
                          id UUID NOT NULL PRIMARY KEY,
                          ticket_id UUID NOT NULL,
                          sender_type VARCHAR(20) NOT NULL,
                          body TEXT NOT NULL,
                          sent_at TIMESTAMP NOT NULL,

                          CONSTRAINT fk_messages_ticket
                              FOREIGN KEY (ticket_id)
                                  REFERENCES tickets(id)
);

CREATE TABLE chat_sessions (
                               id UUID NOT NULL PRIMARY KEY,
                               ticket_id UUID NOT NULL UNIQUE,
                               session_token VARCHAR(255) NOT NULL UNIQUE,
                               created_at TIMESTAMP NOT NULL,

                               CONSTRAINT fk_chat_sessions_ticket
                                   FOREIGN KEY (ticket_id)
                                       REFERENCES tickets(id)
);

CREATE INDEX idx_tickets_status_created_at
    ON tickets(status, created_at);

CREATE INDEX idx_messages_ticket_id
    ON messages(ticket_id);