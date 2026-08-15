package com.livedesk.auth.service;

import com.livedesk.auth.session_token.CustomerPrincipal;
import com.livedesk.ticket.domain.Ticket;
import com.livedesk.ticket.service.TicketService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

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

        if (principal instanceof CustomerPrincipal(UUID id)) {
            if (!ticket.getId()
                    .equals(id)) {

                throw new AccessDeniedException(
                        "Not authorized to access this ticket"
                );
            }
            return;
        }

        if (authentication.getAuthorities().stream()
                .anyMatch(a ->
                        "ROLE_AGENT".equals(a.getAuthority()))) {

            // Temporary rule:
            // agents can access any ticket
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
}