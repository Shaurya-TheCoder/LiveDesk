package com.livedesk.ticket.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    private UUID id;
    private String firstMessage;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "assigned_agent_id")
    private UUID assignedAgentId;

    protected Ticket() {}

    public Ticket(String firstMessage, LocalDateTime createdAt) {
        if(firstMessage == null || firstMessage.isBlank()) {
            throw new IllegalArgumentException("message must not be null or blank");
        }
        Objects.requireNonNull(createdAt, "ticket creation date should not be null.");
        this.id = UUID.randomUUID();
        this.firstMessage = firstMessage;
        this.status = TicketStatus.OPEN;
        this.createdAt = createdAt;
        this.assignedAgentId = null;
    }
    public Optional<UUID> getId() {
        return Optional.ofNullable(id);
    }
    public String getFirstMessage(){
        return firstMessage;
    }
    public TicketStatus getStatus(){
        return status;
    }
    public LocalDateTime getCreatedAt(){
        return createdAt;
    }
    public UUID getAssignedAgentId() {
        return assignedAgentId;
    }
    public void assign(){
        if(status != TicketStatus.OPEN && status != TicketStatus.QUEUED) {
            throw new IllegalStateException("Cannot assign ticket in status " + status + ". Only OPEN or QUEUED tickets can be assigned.");
        }
        status = TicketStatus.ASSIGNED;
    }
    public void queue(){
        if(status != TicketStatus.OPEN) {
            throw new IllegalStateException("Cannot queue a ticket that is not OPEN. Current status: " + status);
        }
        status = TicketStatus.QUEUED;
    }

    public void resolve(){
        if(status != TicketStatus.ASSIGNED) {
            throw new IllegalStateException("Cannot resolve a ticket that is not ASSIGNED. Current status: " + status);
        }
        status = TicketStatus.RESOLVED;
    }
    public void close(){
        if(status != TicketStatus.RESOLVED) {
            throw new IllegalStateException(
                    "Cannot close a ticket that is not RESOLVED. Current status: " + status
            );
        }
        status = TicketStatus.CLOSED;
    }
}
