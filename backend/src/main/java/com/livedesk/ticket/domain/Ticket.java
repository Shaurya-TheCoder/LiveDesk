package com.livedesk.ticket.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    private UUID id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "assigned_agent_id")
    private UUID assignedAgentId;

    protected Ticket() {}

    public Ticket(LocalDateTime createdAt) {
        this.id = UUID.randomUUID();
        this.status = TicketStatus.OPEN;
        this.createdAt = createdAt;
        this.assignedAgentId = null;
    }

    public UUID getId() {
        return id;
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
