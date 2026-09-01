package com.livedesk.ticket.service;

import com.livedesk.agent.domain.Agent;
import com.livedesk.agent.repository.AgentRepository;
import com.livedesk.agent.service.AgentPresenceService;
import com.livedesk.events.dto.TicketAssignedEvent;
import com.livedesk.ticket.domain.Ticket;
import com.livedesk.ticket.repository.TicketRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class RoutingService {
    private final AgentRepository agentRepository;
    private final TicketRepository ticketRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final AgentPresenceService agentPresenceService;

    public RoutingService(AgentPresenceService agentPresenceService, AgentRepository agentRepository, TicketRepository ticketRepository, ApplicationEventPublisher eventPublisher){
        this.agentRepository = agentRepository;
        this.ticketRepository = ticketRepository;
        this.eventPublisher = eventPublisher;
        this.agentPresenceService = agentPresenceService;
    }

    @Transactional
    public void assignTicket(Ticket ticket) {
        Optional<Agent> availableAgent = findAvailableAgent();

        if (availableAgent.isEmpty()) {
            queueTicket(ticket);
            return;
        }

        Agent agent = availableAgent.get();

        assignTicketToAgent(ticket, agent);

        persistAssignment(ticket, agent);

        publishAssignmentEvent(ticket, agent);
    }

    @Transactional
    public void tryAssignNextQueuedTicket(){
        Ticket ticket = ticketRepository.findOldestQueuedTicketForUpdate().orElse(null);
        if(ticket != null) {
            assignTicket(ticket);
        }
    }

    private void queueTicket(Ticket ticket) {
        ticket.queue();
        ticketRepository.save(ticket);
    }

    private void assignTicketToAgent(Ticket ticket, Agent agent) {
        agent.incrementActiveChatCount();
        ticket.assign(agent.getId());
    }

    private void persistAssignment(Ticket ticket, Agent agent) {
        agentRepository.save(agent);
        ticketRepository.save(ticket);
    }

    private void publishAssignmentEvent(Ticket ticket, Agent agent) {
        eventPublisher.publishEvent(
                new TicketAssignedEvent(
                        ticket.getId(),
                        agent.getId()
                )
        );
    }

    private Optional<Agent> findAvailableAgent(){
        List<UUID> candidateIds = agentRepository.findCapacityAvailableAgentIds();

        for(UUID candidate : candidateIds){
            if(!agentPresenceService.isOnline(candidate))
                continue;

            Optional<Agent> agent = agentRepository.lockAgentIfAvailable(candidate);

            if(agent.isPresent())
                return agent;
        }

        return Optional.empty();
    }
}
