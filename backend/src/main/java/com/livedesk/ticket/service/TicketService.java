package com.livedesk.ticket.service;

import com.livedesk.ticket.domain.Ticket;
import com.livedesk.ticket.exception.TicketNotFoundException;
import com.livedesk.ticket.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

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

    public Ticket getTicket(UUID id) {
        return ticketRepository.findById(id)
                .orElseThrow(() ->
                        new TicketNotFoundException("Ticket not found: " + id));
    }
}
