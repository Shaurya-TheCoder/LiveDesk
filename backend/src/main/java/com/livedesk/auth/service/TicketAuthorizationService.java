package com.livedesk.auth.service;

import com.livedesk.agent.dto.AgentPrincipal;
import com.livedesk.auth.session_token.CustomerPrincipal;
import com.livedesk.ticket.domain.Ticket;
import com.livedesk.ticket.service.TicketService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
public class TicketAuthorizationService {

    private final TicketService ticketService;

    public TicketAuthorizationService(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    public void verifyAccess(
            UUID ticketId,
            Authentication authentication
    ) {
        Ticket ticket = ticketService.getTicket(ticketId);

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomerPrincipal customerPrincipal) {
            verifyCustomerAccess(ticket, customerPrincipal);
            return;
        }

        if (principal instanceof AgentPrincipal agentPrincipal) {
            verifyAgentAccess(ticket, agentPrincipal);
            return;
        }

        throw new AccessDeniedException(
                "Not authorized to access this ticket"
        );
    }

    public void verifyCustomerAccess(
            Ticket ticket,
            CustomerPrincipal principal){
        if (!ticket.getId().equals(principal.ticketId())) {
            throw new AccessDeniedException(
                    "Not authorized to access this ticket");
        }
    }
    public void verifyAgentAccess(
            Ticket ticket, AgentPrincipal principal
    ){
        UUID assignedAgentId = ticket.getAssignedAgentId();
        if(assignedAgentId == null || !assignedAgentId.equals(Objects.requireNonNull(principal).agentId())){
            throw new AccessDeniedException(
                    "Not authorized to access this ticket"
            );
        }
    }

    public void verifyAssignedAgent(
            UUID ticketId, Authentication authentication
    ){
        if (authentication.getPrincipal() instanceof AgentPrincipal agentPrincipal) {
            Ticket ticket = ticketService.getTicket(ticketId);
            verifyAgentAccess(ticket, agentPrincipal);
        }else{
            throw new AccessDeniedException(
                    "Only agents are allowed to resolve tickets"
            );
        }

    }
}