package com.livedesk.ticket.service;

import com.livedesk.agent.domain.Agent;
import com.livedesk.agent.repository.AgentRepository;
import com.livedesk.events.TicketAssignedEvent;
import com.livedesk.ticket.domain.Ticket;
import com.livedesk.ticket.repository.TicketRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class RoutingService {
    private final AgentRepository agentRepository;
    private final TicketRepository ticketRepository;
    private final ApplicationEventPublisher eventPublisher;

    public RoutingService(AgentRepository agentRepository, TicketRepository ticketRepository, ApplicationEventPublisher eventPublisher){
        this.agentRepository = agentRepository;
        this.ticketRepository = ticketRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void assignTicket(Ticket ticket) {
        Optional<Agent> availableAgent =
                agentRepository.findAvailableAgentForUpdate();

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
}
