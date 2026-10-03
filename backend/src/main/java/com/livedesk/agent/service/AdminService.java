package com.livedesk.agent.service;

import com.livedesk.agent.domain.Agent;
import com.livedesk.agent.domain.Role;
import com.livedesk.agent.dto.AdminResolvedTicketResponse;
import com.livedesk.agent.dto.AdminSystemStatsResponse;
import com.livedesk.agent.dto.AgentDetailsResponse;
import com.livedesk.agent.dto.AgentResponse;
import com.livedesk.agent.repository.AgentRepository;
import com.livedesk.messenger.domain.ChatMessage;
import com.livedesk.messenger.repository.ChatMessageRepository;
import com.livedesk.ticket.domain.Ticket;
import com.livedesk.ticket.domain.TicketPriority;
import com.livedesk.ticket.domain.TicketStatus;
import com.livedesk.ticket.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class AdminService {
    private final AgentRepository agentRepository;
    private final TicketRepository ticketRepository;
    private final AgentPresenceService agentPresenceService;
    private final ChatMessageRepository chatMessageRepository;


    public AdminService(AgentRepository agentRepository, TicketRepository ticketRepository,
                        AgentPresenceService agentPresenceService, ChatMessageRepository chatMessageRepository) {
        this.agentRepository = agentRepository;
        this.ticketRepository = ticketRepository;
        this.agentPresenceService = agentPresenceService;
        this.chatMessageRepository = chatMessageRepository;
    }

    @Transactional(readOnly = true)
    public List<AgentResponse> getAllAgents() {

        return agentRepository.findByRole(Role.AGENT)
                .stream()
                .map(agent -> new AgentResponse(
                        agent.getId(),
                        agent.getName(),
                        agent.getEmail(),
                        agent.getRole(),
                        agent.getActiveChatCount()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public AdminSystemStatsResponse getSystemStats() {

        long totalCreated = ticketRepository.count();
        long queued = ticketRepository.countByStatus(TicketStatus.QUEUED);
        long escalated = ticketRepository.countByPriority(TicketPriority.ESCALATED);
        long closed = ticketRepository.countByStatus(TicketStatus.CLOSED);

        return new AdminSystemStatsResponse(
                totalCreated,
                queued,
                escalated,
                closed
        );
    }

    @Transactional(readOnly = true)
    public AgentDetailsResponse getAgentDetails(UUID agentId) {

        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Agent not found: " + agentId
                        )
                );

        long assignedTicketCount =
                ticketRepository.countByAssignedAgentIdAndStatus(
                        agentId,
                        TicketStatus.ASSIGNED
                );

        long resolvedTicketCount =
                ticketRepository.countByAssignedAgentIdAndStatus(
                        agentId,
                        TicketStatus.RESOLVED
                );
        long closedTicketCount = ticketRepository.countByAssignedAgentIdAndStatus(
                agentId,
                TicketStatus.CLOSED
        );

        boolean online = agentPresenceService.isOnline(agentId);

        return new AgentDetailsResponse(
                agent.getId(),
                agent.getName(),
                agent.getEmail(),
                agent.getRole(),
                online,
                assignedTicketCount,
                resolvedTicketCount+closedTicketCount
        );
    }

    @Transactional(readOnly = true)
    public List<AdminResolvedTicketResponse> getAgentResolvedTickets(
            UUID agentId
    ) {
        agentRepository.findById(agentId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Agent not found: " + agentId
                        )
                );

        List<Ticket> tickets =
                ticketRepository.findByAssignedAgentIdAndStatus(
                        agentId,
                        TicketStatus.RESOLVED
                );

        return tickets.stream()
                .map(ticket -> {

                    LocalDateTime lastActivityAt =
                            chatMessageRepository
                                    .findTopByTicketIdOrderByCreatedAtDesc(
                                            ticket.getId()
                                    )
                                    .map(ChatMessage::getCreatedAt)
                                    .orElse(null);

                    return new AdminResolvedTicketResponse(
                            ticket.getId(),
                            ticket.getSubject(),
                            ticket.getStatus(),
                            ticket.getPriority(),
                            ticket.getAssignedAgentId(),
                            ticket.getCreatedAt(),
                            lastActivityAt
                    );
                })
                .toList();
    }

    @Transactional
    public void closeTicket(UUID ticketId) {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Ticket not found: " + ticketId
                        )
                );

        ticket.close();
    }
}