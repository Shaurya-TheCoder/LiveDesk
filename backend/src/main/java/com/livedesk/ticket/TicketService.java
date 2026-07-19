package com.livedesk.ticket;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository){
        this.ticketRepository = ticketRepository;
    }

    public Ticket createTicket(String subject){
        Ticket ticket = new Ticket(subject, LocalDateTime.now());
        return ticketRepository.save(ticket);
    }
    public Optional<Ticket> getTicket(Long id) {
        return ticketRepository.findById(id);
    }
}
