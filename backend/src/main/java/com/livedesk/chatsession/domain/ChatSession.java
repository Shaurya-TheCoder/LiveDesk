package com.livedesk.chatsession.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "chat_sessions")
public class ChatSession {
    @Id
    private UUID id;

    @Column(name = "ticked_id", nullable = false)
    private UUID ticketId;

    @Column(name = "session_token", nullable = false, unique = true)
    private String sessionToken;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected ChatSession() {}

    public ChatSession(UUID ticketId, String sessionToken, LocalDateTime createdAt) {
        Objects.requireNonNull(ticketId, "ticketId must not be null");
        if (sessionToken == null || sessionToken.isBlank()) {
            throw new IllegalArgumentException("sessionToken must not be null or blank");
        }
        Objects.requireNonNull(createdAt, "createdAt must not be null");

        this.id = UUID.randomUUID();
        this.ticketId = ticketId;
        this.sessionToken = sessionToken;
        this.createdAt = createdAt;
    }

    public Optional<UUID> getId() { return Optional.ofNullable(id); }
    public UUID getTicketId() { return ticketId; }
    public String getSessionToken() { return sessionToken; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}