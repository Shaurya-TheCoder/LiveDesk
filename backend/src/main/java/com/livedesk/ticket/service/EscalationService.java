package com.livedesk.ticket.service;

import com.livedesk.messenger.domain.ChatMessage;
import com.livedesk.messenger.domain.MessageSender;
import com.livedesk.messenger.repository.ChatMessageRepository;
import com.livedesk.ticket.domain.Ticket;
import com.livedesk.ticket.domain.TicketPriority;
import com.livedesk.ticket.domain.TicketStatus;
import com.livedesk.ticket.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EscalationService {
    private static final Duration QUEUED_TIMEOUT = Duration.ofMinutes(5);
    private static final Duration UNANSWERED_TIMEOUT = Duration.ofMinutes(3);

    private final ChatMessageRepository chatMessageRepository;
    private final TicketRepository ticketRepository;

    public EscalationService(ChatMessageRepository chatMessageRepository, TicketRepository ticketRepository){
        this.chatMessageRepository = chatMessageRepository;
        this.ticketRepository = ticketRepository;
    }

    public void processEscalations(){
        checkQueuedTickets();
        checkAssignedTickets();
    }

    public void checkQueuedTickets(){
        LocalDateTime now = LocalDateTime.now();
        List<Ticket> queuedTickets = ticketRepository.findByStatus(TicketStatus.QUEUED);
        for(Ticket ticket : queuedTickets){
            if(ticket.getPriority().equals(TicketPriority.ESCALATED)){
                continue;
            }
            if(ticket.getCreatedAt().isBefore(now.minusMinutes(QUEUED_TIMEOUT.toMinutes()))){
                ticket.escalate();

                ticketRepository.save(ticket);
            }
        }
    }

    public void checkAssignedTickets(){
        LocalDateTime now = LocalDateTime.now();
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
                    ticket.escalate();
                    ticketRepository.save(ticket);
                }
                continue;
            }

            if(lastCustomerMessage.getCreatedAt().isAfter(lastAgentMessage.getCreatedAt()) &&
                    lastCustomerMessage.getCreatedAt().isBefore(now.minusMinutes(UNANSWERED_TIMEOUT.toMinutes()))){
                ticket.escalate();
                ticketRepository.save(ticket);
            }

        }
    }
}
