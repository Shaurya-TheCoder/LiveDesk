package com.livedesk.ticket.service;


import com.livedesk.events.dto.TicketEscalatedEvent;
import com.livedesk.messenger.domain.ChatMessage;
import com.livedesk.messenger.domain.MessageSender;
import com.livedesk.messenger.repository.ChatMessageRepository;
import com.livedesk.ticket.domain.Ticket;
import com.livedesk.ticket.domain.TicketPriority;
import com.livedesk.ticket.domain.TicketStatus;
import com.livedesk.ticket.repository.TicketRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class EscalationService {
    private static final Duration QUEUED_TIMEOUT = Duration.ofMinutes(5);
    private static final Duration UNANSWERED_TIMEOUT = Duration.ofMinutes(3);

    private final ApplicationEventPublisher eventPublisher;
    private final ChatMessageRepository chatMessageRepository;
    private final TicketRepository ticketRepository;

    public EscalationService(ChatMessageRepository chatMessageRepository, TicketRepository ticketRepository, ApplicationEventPublisher eventPublisher){
        this.chatMessageRepository = chatMessageRepository;
        this.ticketRepository = ticketRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void processEscalations(){
        LocalDateTime now = LocalDateTime.now();
        long totalQueuedTicketsEscalated = checkQueuedTickets(now);
        long totalAssignedTicketsEscalated = checkAssignedTickets(now);
    }

    public long checkQueuedTickets(LocalDateTime now){
        long totalEscalatedTickets = 0L;
        List<Ticket> queuedTickets = ticketRepository.findByStatus(TicketStatus.QUEUED);
        for(Ticket ticket : queuedTickets){
            if(ticket.getPriority().equals(TicketPriority.ESCALATED)){
                continue;
            }
            if(ticket.getCreatedAt().isBefore(now.minusMinutes(QUEUED_TIMEOUT.toMinutes()))){
                escalateTicket(ticket);
                totalEscalatedTickets += 1;
                publishEscalatedEvent(ticket.getId(), null);
            }
        }
        return totalEscalatedTickets;
    }

    public long checkAssignedTickets(LocalDateTime now){

        long totalEscalatedTickets = 0L;
        List<Ticket> assignedTickets = ticketRepository.findByStatus(TicketStatus.ASSIGNED);

        for(Ticket ticket : assignedTickets){
            if(ticket.getPriority().equals(TicketPriority.ESCALATED)){
                continue;
            }
            ChatMessage lastCustomerMessage = chatMessageRepository
                    .findTopByTicketIdAndSenderOrderByCreatedAtDesc(ticket.getId(), MessageSender.CUSTOMER).orElse(null);

            ChatMessage lastAgentMessage = chatMessageRepository
                    .findTopByTicketIdAndSenderOrderByCreatedAtDesc(ticket.getId(), MessageSender.AGENT).orElse(null);

            if(lastCustomerMessage == null)
                continue;

            if(lastAgentMessage == null){
                if(lastCustomerMessage.getCreatedAt().isBefore(now.minusMinutes(UNANSWERED_TIMEOUT.toMinutes()))){
                    escalateTicket(ticket);
                    totalEscalatedTickets += 1;
                    publishEscalatedEvent(ticket.getId(), ticket.getAssignedAgentId());
                }
                continue;
            }

            if(lastCustomerMessage.getCreatedAt().isAfter(lastAgentMessage.getCreatedAt()) &&
                    lastCustomerMessage.getCreatedAt().isBefore(now.minusMinutes(UNANSWERED_TIMEOUT.toMinutes()))){

                escalateTicket(ticket);
                totalEscalatedTickets += 1;
                publishEscalatedEvent(ticket.getId(), ticket.getAssignedAgentId());

            }
        }
        return totalEscalatedTickets;
    }
    private void publishEscalatedEvent(UUID ticketId, UUID agentId) {
        eventPublisher.publishEvent(
                new TicketEscalatedEvent(
                        ticketId,
                        agentId
                )
        );
    }
    private void escalateTicket(Ticket ticket){
        ticket.escalate();
        ticketRepository.save(ticket);
    }
}
