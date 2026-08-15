package com.livedesk.ticket.service;

import com.livedesk.messenger.domain.ChatMessage;
import com.livedesk.messenger.domain.MessageSender;
import com.livedesk.messenger.repository.ChatMessageRepository;
import com.livedesk.ticket.domain.Ticket;
import com.livedesk.ticket.exception.TicketNotFoundException;
import com.livedesk.ticket.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;
    private final ChatMessageRepository chatMessageRepository;

    public TicketService(TicketRepository ticketRepository, ChatMessageRepository chatMessageRepository){
        this.ticketRepository = ticketRepository;
        this.chatMessageRepository = chatMessageRepository;
    }
    @Transactional // Dono save ya toh ek sath chalenge, ya ek bhi nahi!
    public Ticket createTicket(String message, LocalDateTime now){
        if(message == null || message.isBlank()) {
            throw new IllegalArgumentException("message must not be null or blank");
        }
        Objects.requireNonNull(now, "ticket creation date should not be null.");

        Ticket ticket = new Ticket(now);
        ticket = ticketRepository.save(ticket);

        ChatMessage firstMessage = new ChatMessage(
                ticket.getId(), MessageSender.CUSTOMER, message, now
        );
        chatMessageRepository.save(firstMessage);
        return ticket;
    }

    public Ticket getTicket(UUID id) {
        return ticketRepository.findById(id)
                .orElseThrow(() ->
                        new TicketNotFoundException("Ticket not found: " + id));
    }
}
