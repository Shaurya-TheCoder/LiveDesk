package com.livedesk.ticket.service;

import com.livedesk.agent.domain.Agent;
import com.livedesk.agent.repository.AgentRepository;
import com.livedesk.ticket.domain.Ticket;
import com.livedesk.ticket.repository.TicketRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;


@Service
public class RoutingService {
    private final AgentRepository agentRepository;
    private final TicketRepository ticketRepository;

    public RoutingService(AgentRepository agentRepository, TicketRepository ticketRepository){
        this.agentRepository = agentRepository;
        this.ticketRepository = ticketRepository;
    }

    @Transactional
    public void assignTicket(Ticket ticket){
        Agent agent = agentRepository.findAvailableAgentForUpdate().orElse(null);

        if(agent == null){
            ticket.queue();
        }else {
            agent.incrementActiveChatCount();
            ticket.assign(agent.getId());
            agentRepository.save(agent);
        }
        ticketRepository.save(ticket);
    }

    @Transactional
    public void tryAssignNextQueuedTicket(){
        Ticket ticket = ticketRepository.findOldestQueuedTicketForUpdate().orElse(null);
        if(ticket != null) {
            assignTicket(ticket);
        }
    }
}
