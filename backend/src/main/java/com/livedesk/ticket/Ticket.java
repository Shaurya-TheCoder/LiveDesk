package com.livedesk.ticket;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class Ticket {

    private Long id;
    private String subject;
    private TicketStatus status;
    private final LocalDateTime createdAt;

    public Ticket(String subject, LocalDateTime createdAt) {
        if(subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("subject must not be null or blank");
        }
        Objects.requireNonNull(createdAt, "ticket creation date should not be null.");
        this.subject = subject;
        this.status = TicketStatus.QUEUED;
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

    public String getSubject(){
        return subject;
    }
    public TicketStatus getStatus(){
        return status;
    }
    public LocalDateTime getCreatedAt(){
        return createdAt;
    }
    public void assign(){
        if(status != TicketStatus.QUEUED) {
            throw new IllegalStateException("Cannot assign a ticket that is not QUEUED. Current status: " + status);
        }
        status = TicketStatus.ASSIGNED;
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
