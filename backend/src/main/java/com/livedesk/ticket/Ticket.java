package com.livedesk.ticket;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class Ticket {

    private Long id;
    private String firstMessage;
    private TicketStatus status;
    private final LocalDateTime createdAt;

    public Ticket(String firstMessage, LocalDateTime createdAt) {
        if(firstMessage == null || firstMessage.isBlank()) {
            throw new IllegalArgumentException("subject must not be null or blank");
        }
        Objects.requireNonNull(createdAt, "ticket creation date should not be null.");
        this.firstMessage = firstMessage;
        this.status = TicketStatus.OPEN;
        this.createdAt = createdAt;
    }
    void setId(Long id){
        Objects.requireNonNull(id, "id must not be null");
        if(this.id != null){
            throw new IllegalStateException("Id cannot be re-assigned.");
        }
        this.id = id;
    }
    public Optional<Long> getId(){
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
            throw new IllegalStateException("Cannot close a ticket that is not RESOLVED. Current status: " + status);
        }
        status = TicketStatus.CLOSED;
    }
}
