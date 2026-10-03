package com.livedesk.ticket;

import com.livedesk.ticket.domain.Ticket;
import com.livedesk.ticket.domain.TicketPriority;
import com.livedesk.ticket.domain.TicketStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TicketTest {

    private Ticket newTicket() {
        return new Ticket(LocalDateTime.now(), "Login issue", "ABCD-EFGH-JKLM-NPQR");
    }

    @Test
    void newTicketStartsOpenWithNormalPriorityAndNoAgent() {
        Ticket ticket = newTicket();

        assertAll(
                () -> assertNotNull(ticket.getId()),
                () -> assertEquals(TicketStatus.OPEN, ticket.getStatus()),
                () -> assertEquals(TicketPriority.NORMAL, ticket.getPriority()),
                () -> assertNull(ticket.getAssignedAgentId()),
                () -> assertEquals("Login issue", ticket.getSubject())
        );
    }

    @Test
    void queuedTicketCanLaterBeAssigned() {
        Ticket ticket = newTicket();
        UUID agentId = UUID.randomUUID();

        ticket.queue();
        assertEquals(TicketStatus.QUEUED, ticket.getStatus());

        ticket.assign(agentId);
        assertEquals(TicketStatus.ASSIGNED, ticket.getStatus());
        assertEquals(agentId, ticket.getAssignedAgentId());
    }

    @Test
    void queueingTwiceThrows() {
        Ticket ticket = newTicket();
        ticket.queue();

        assertThrows(IllegalStateException.class, ticket::queue);
    }

    @Test
    void assigningAnAlreadyAssignedTicketThrows() {
        Ticket ticket = newTicket();
        ticket.assign(UUID.randomUUID());

        assertThrows(IllegalStateException.class, () -> ticket.assign(UUID.randomUUID()));
    }

    @Test
    void resolveRequiresAssignedStatus() {
        Ticket ticket = newTicket();

        assertThrows(IllegalStateException.class, ticket::resolve);
    }

    @Test
    void closeRequiresResolvedStatusAndFullLifecycleWorks() {
        Ticket ticket = newTicket();
        ticket.assign(UUID.randomUUID());

        assertThrows(IllegalStateException.class, ticket::close);

        ticket.resolve();
        ticket.close();
        assertEquals(TicketStatus.CLOSED, ticket.getStatus());
    }

    @Test
    void escalateRaisesPriorityWithoutChangingStatus() {
        Ticket ticket = newTicket();
        ticket.queue();

        ticket.escalate();

        assertEquals(TicketPriority.ESCALATED, ticket.getPriority());
        assertEquals(TicketStatus.QUEUED, ticket.getStatus());
    }
}