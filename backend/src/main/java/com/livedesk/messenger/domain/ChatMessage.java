package com.livedesk.messenger.domain;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class ChatMessage {
    private Long id;
    private final Long ticketId;
    private final MessageSender sender;
    private final String content;
    private final LocalDateTime createdAt;

    public ChatMessage(
            Long ticketId,
            MessageSender sender,
            String content,
            LocalDateTime createdAt){

        Objects.requireNonNull(ticketId, "ticketId must not be null");
        Objects.requireNonNull(sender, "sender must not be null");
        Objects.requireNonNull(createdAt, "message creation date must not be null");

        if(content == null || content.isBlank()){
            throw new IllegalArgumentException(
                    "message content must not be null or blank");
        }

        this.ticketId = ticketId;
        this.sender = sender;
        this.content = content;
        this.createdAt = createdAt;
    }

    public void setId(Long id) {
        Objects.requireNonNull(id, "id must not be null");

        if (this.id != null) {
            throw new IllegalStateException("Id cannot be re-assigned.");
        }

        this.id = id;
    }
    public Optional<Long> getId(){
        return Optional.ofNullable(id);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public MessageSender getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public Long getTicketId() {
        return ticketId;
    }
}
