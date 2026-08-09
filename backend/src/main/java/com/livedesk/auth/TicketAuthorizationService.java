package com.livedesk.auth;

import com.livedesk.auth.session_token.CustomerPrincipal;
import com.livedesk.ticket.Ticket;
import com.livedesk.ticket.TicketService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class TicketAuthorizationService {

    private final TicketService ticketService;

    public TicketAuthorizationService(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    public void verifyAccess(
            Long ticketId,
            Authentication authentication
    ) {
        Ticket ticket = ticketService.getTicket(ticketId);

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomerPrincipal customer) {
            if (!ticket.getId().orElseThrow()
                    .equals(customer.getTicketId())) {

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
        if (!ticket.getId().orElseThrow().equals(principal.getTicketId())) {
            throw new AccessDeniedException(
                    "Not authorized to access this ticket");
        }
    }
}