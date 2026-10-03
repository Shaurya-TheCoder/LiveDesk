package com.livedesk.ticket;

import com.livedesk.ticket.service.TicketService;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

class TicketServiceValidationTest {

    private final TicketService ticketService = new TicketService(null, null, null, null, null, null);

    @Test
    void createTicketRejectsBlankMessageOrSubject() {
        LocalDateTime now = LocalDateTime.now();

        assertThrows(IllegalArgumentException.class,
                () -> ticketService.createTicket("   ", now, "Subject", "CODE"));
        assertThrows(IllegalArgumentException.class,
                () -> ticketService.createTicket("Hello", now, "", "CODE"));
    }

    @Test
    void recoverTicketRejectsBlankRecoveryCode() {
        assertThrows(IllegalArgumentException.class, () -> ticketService.recoverTicket(" "));
        assertThrows(IllegalArgumentException.class, () -> ticketService.recoverTicket(null));
    }
}
