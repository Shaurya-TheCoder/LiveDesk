package com.livedesk.auth.service;

import com.livedesk.agent.domain.Role;
import com.livedesk.agent.dto.AgentPrincipal;
import com.livedesk.auth.session_token.CustomerPrincipal;
import com.livedesk.ticket.domain.Ticket;
import com.livedesk.ticket.service.TicketService;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


class TicketAuthorizationServiceTest {

    private final TicketAuthorizationService service =
            new TicketAuthorizationService(new TicketService(null, null, null, null, null, null));

    private Ticket newTicket() {
        return new Ticket(LocalDateTime.now(), "Subject", "CODE-CODE-CODE-CODE");
    }

    @Test
    void customerCanOnlyAccessTheirOwnTicket() {
        Ticket ticket = newTicket();

        assertDoesNotThrow(() ->
                service.verifyCustomerAccess(ticket, new CustomerPrincipal(ticket.getId())));
        assertThrows(AccessDeniedException.class, () ->
                service.verifyCustomerAccess(ticket, new CustomerPrincipal(UUID.randomUUID())));
    }

    @Test
    void assignedAgentCanAccessButOtherAgentCannot() {
        Ticket ticket = newTicket();
        UUID assignedAgent = UUID.randomUUID();
        ticket.assign(assignedAgent);

        AgentPrincipal owner = new AgentPrincipal(assignedAgent, "owner@x.com", Role.AGENT);
        AgentPrincipal stranger = new AgentPrincipal(UUID.randomUUID(), "other@x.com", Role.AGENT);

        assertDoesNotThrow(() -> service.verifyAgentAccess(ticket, owner));
        assertThrows(AccessDeniedException.class, () -> service.verifyAgentAccess(ticket, stranger));
    }

    @Test
    void agentIsDeniedOnTicketThatIsNotAssignedYet() {
        Ticket unassigned = newTicket(); // assignedAgentId is null
        AgentPrincipal agent = new AgentPrincipal(UUID.randomUUID(), "a@x.com", Role.AGENT);

        assertThrows(AccessDeniedException.class, () -> service.verifyAgentAccess(unassigned, agent));
    }
}