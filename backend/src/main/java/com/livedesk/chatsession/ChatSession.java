package com.livedesk.chatsession;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class ChatSession {
    private Long id;
    private final Long ticketId;
    private final String sessionToken;
    private final LocalDateTime createdAt;

    public ChatSession(Long ticketId, String sessionToken, LocalDateTime createdAt) {
        Objects.requireNonNull(ticketId, "ticketId must not be null");
        if (sessionToken == null || sessionToken.isBlank()) {
            throw new IllegalArgumentException("sessionToken must not be null or blank");
        }
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.ticketId = ticketId;
        this.sessionToken = sessionToken;
        this.createdAt = createdAt;
    }

    void setId(Long id) {
        Objects.requireNonNull(id, "id must not be null");
        if (this.id != null) {
            throw new IllegalStateException("Id cannot be re-assigned.");
        }
        this.id = id;
    }

    public Optional<Long> getId() { return Optional.ofNullable(id); }
    public Long getTicketId() { return ticketId; }
    public String getSessionToken() { return sessionToken; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}