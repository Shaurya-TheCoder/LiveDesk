package com.livedesk.ticket;

import com.livedesk.auth.session_token.CustomerPrincipal;
import com.livedesk.ticket.exception.TicketNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository){
        this.ticketRepository = ticketRepository;
    }

    public Ticket createTicket(String firstMessage){
        Ticket ticket = new Ticket(firstMessage, LocalDateTime.now());
        return ticketRepository.save(ticket);
    }

    public Ticket getTicket(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() ->
                        new TicketNotFoundException("Ticket not found: " + id));
    }
    public void verifyCustomerAccess(
            Ticket ticket,
            CustomerPrincipal principal){
        if (!ticket.getId().orElseThrow().equals(principal.getTicketId())) {
            throw new AccessDeniedException(
                    "Not authorized to access this ticket");
        }
    }
}
