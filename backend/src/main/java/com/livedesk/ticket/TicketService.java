package com.livedesk.ticket;

import com.livedesk.ticket.exception.TicketNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
}
