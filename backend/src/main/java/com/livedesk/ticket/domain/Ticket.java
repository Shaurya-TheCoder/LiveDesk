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

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TicketPriority priority;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "assigned_agent_id")
    private UUID assignedAgentId;

    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "recovery_code_hash", nullable = false)
    private String recoveryCodeHash;

    protected Ticket() {}

    public Ticket(LocalDateTime createdAt, String subject, String recoveryCodeHash) {
        this.id = UUID.randomUUID();
        this.status = TicketStatus.OPEN;
        this.createdAt = createdAt;
        this.assignedAgentId = null;
        this.priority = TicketPriority.NORMAL;
        this.recoveryCodeHash = recoveryCodeHash;
        this.subject = subject;
    }

    public UUID getId() {
        return id;
    }

    public String getRecoveryCodeHash() {
        return recoveryCodeHash;
    }

    public String getSubject() {
        return subject;
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
    public TicketPriority getPriority() { return priority; }
    public void assign(UUID agentId){
        if(status != TicketStatus.OPEN && status != TicketStatus.QUEUED) {
            throw new IllegalStateException("Cannot assign ticket in status " + status + ". Only OPEN or QUEUED tickets can be assigned.");
        }
        assignedAgentId = agentId;
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
    public void escalate(){
        this.priority = TicketPriority.ESCALATED;
    }


}
