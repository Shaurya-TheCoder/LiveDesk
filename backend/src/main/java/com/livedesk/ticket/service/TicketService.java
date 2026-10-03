package com.livedesk.ticket.service;

import com.livedesk.agent.domain.Agent;
import com.livedesk.agent.repository.AgentRepository;
import com.livedesk.chatsession.domain.ChatSession;
import com.livedesk.chatsession.service.ChatSessionService;
import com.livedesk.messenger.domain.ChatMessage;
import com.livedesk.messenger.domain.MessageSender;
import com.livedesk.messenger.repository.ChatMessageRepository;
import com.livedesk.ticket.HashUtil;
import com.livedesk.ticket.RecoveryCodeGenerator;
import com.livedesk.ticket.domain.Ticket;
import com.livedesk.ticket.domain.TicketStatus;
import com.livedesk.events.dto.TicketResolvedEvent;
import com.livedesk.ticket.dto.AgentTicketResponse;
import com.livedesk.ticket.exception.TicketNotFoundException;
import com.livedesk.ticket.repository.TicketRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final AgentRepository agentRepository;
    private final RoutingService routingService;
    private final ApplicationEventPublisher eventPublisher;
    private final ChatSessionService chatSessionService;

    public TicketService(
            ApplicationEventPublisher eventPublisher,
            TicketRepository ticketRepository,
            ChatMessageRepository chatMessageRepository,
            RoutingService routingService,
            AgentRepository agentRepository,
            ChatSessionService chatSessionService
    ) {
        this.ticketRepository = ticketRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.routingService = routingService;
        this.agentRepository = agentRepository;
        this.eventPublisher = eventPublisher;
        this.chatSessionService = chatSessionService;
    }
    public Ticket getTicket(UUID id) {
        return ticketRepository.findById(id)
                .orElseThrow(() ->
                        new TicketNotFoundException("Ticket not found: " + id));
    }
    public long getQueuePosition(Ticket ticket){
        if(!ticket.getStatus().equals(TicketStatus.QUEUED)){
            return 0;
        }
        return ticketRepository.findQueuePosition(ticket.getCreatedAt());
    }
    public List<AgentTicketResponse> getAgentAssignedTickets(UUID agentId) {
        return ticketRepository
                .findByAssignedAgentIdAndStatus(agentId, TicketStatus.ASSIGNED)
                .stream()
                .map(ticket -> new AgentTicketResponse(
                        ticket.getId(),
                        ticket.getStatus(),
                        ticket.getPriority(),
                        ticket.getSubject(),
                        ticket.getCreatedAt()
                ))
                .toList();
    }
    public List<AgentTicketResponse> getAgentResolvedTickets(UUID agentId){
            return ticketRepository
                    .findByAssignedAgentIdAndStatus(agentId, TicketStatus.RESOLVED)
                    .stream()
                    .map(ticket -> new AgentTicketResponse(
                            ticket.getId(),
                            ticket.getStatus(),
                            ticket.getPriority(),
                            ticket.getSubject(),
                            ticket.getCreatedAt()
                    ))
                    .toList();
        }

    @Transactional // Dono save ya toh ek sath chalenge, ya ek bhi nahi!
    public Ticket createTicket(String message, LocalDateTime now, String subject ,String recoveryCode){
        if(message == null || message.isBlank()) {
            throw new IllegalArgumentException("message must not be null or blank");
        }
        if(subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("subject must not be null or blank");
        }
        Objects.requireNonNull(now, "ticket creation date should not be null.");
        Objects.requireNonNull(recoveryCode, "recovery code should not be null");

        Ticket ticket = new Ticket(now, subject, recoveryCode); //HashUtil.sha256(recoveryCode)

        ticket = ticketRepository.save(ticket);
        ChatMessage firstMessage = new ChatMessage(
                ticket.getId(), MessageSender.CUSTOMER, message, now
        );

        chatMessageRepository.save(firstMessage);
        routingService.assignTicket(ticket);

        return ticket;
    }

    @Transactional
    public void resolveTicket(UUID ticketId){
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(
                () -> new TicketNotFoundException("Ticket not found with id: " + ticketId)
        );

        Agent agent = agentRepository.findById(ticket.getAssignedAgentId()).orElseThrow(
                () -> new IllegalStateException("No agents found with agent id :"+ticket.getAssignedAgentId())
        );

        ticket.resolve();
        agent.decrementActiveChatCount();

        ticketRepository.save(ticket);
        agentRepository.save(agent);

        eventPublisher.publishEvent(new TicketResolvedEvent());
    }

    public ChatSession recoverTicket(String recoveryCode) {
        if (recoveryCode == null || recoveryCode.isBlank()) {
            throw new IllegalArgumentException("Recovery code must not be blank");
        }

        //String submittedHash = HashUtil.sha256(recoveryCode);

        Ticket ticket = ticketRepository.findByRecoveryCodeHash(recoveryCode)
                .orElseThrow(() ->
                        new TicketNotFoundException("Invalid recovery code")
                );
        return chatSessionService.rotateSessionToken(ticket.getId());
    }
}
