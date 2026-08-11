package com.livedesk.messenger.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "messages")
public class ChatMessage {

    @Id
    private UUID id;

    @Column(name = "ticket_id", nullable = false)
    private UUID ticketId;

    @Enumerated(EnumType.STRING)
    private MessageSender sender;

    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    protected ChatMessage() {}

    public ChatMessage(
            UUID ticketId,
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
        this.id = UUID.randomUUID();
        this.ticketId = ticketId;
        this.sender = sender;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Optional<UUID> getId(){
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

    public UUID getTicketId() {
        return ticketId;
    }
}
