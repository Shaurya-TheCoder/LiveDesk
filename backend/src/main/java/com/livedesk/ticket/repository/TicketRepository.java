package com.livedesk.ticket.repository;

import com.livedesk.ticket.domain.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {

}