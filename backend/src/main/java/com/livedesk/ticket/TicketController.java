package com.livedesk.ticket;

import com.livedesk.auth.session_token.CustomerPrincipal;
import com.livedesk.chatsession.ChatSession;
import com.livedesk.chatsession.ChatSessionService;
import com.livedesk.ticket.dto.CreateTicketRequest;
import com.livedesk.ticket.dto.CreateTicketResponse;
import com.livedesk.ticket.dto.GetTicketResponse;
import com.livedesk.ticket.exception.TicketNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1")
public class TicketController {
    private final TicketService ticketService;
    private final ChatSessionService chatSessionService;

    public TicketController(TicketService ticketService, ChatSessionService chatSessionService){
        this.ticketService = ticketService;
        this.chatSessionService = chatSessionService;
    }
    @GetMapping("/tickets/{id}")
    public ResponseEntity<GetTicketResponse> getTicket(
            @PathVariable Long id,
            Authentication authentication) {

        Ticket ticket = ticketService.getTicket(id);

        if (authentication.getPrincipal() instanceof CustomerPrincipal principal) {
            ticketService.verifyCustomerAccess(ticket, principal);
        }

        GetTicketResponse response = new GetTicketResponse(
                ticket.getId().orElseThrow(
                        () -> new IllegalStateException("ticket id cannot be null")
                ),
                ticket.getStatus(),
                ticket.getCreatedAt()
        );

        return ResponseEntity.ok(response);
    }
    @PostMapping("/tickets")
    public ResponseEntity<CreateTicketResponse> createTicket(@Valid @RequestBody CreateTicketRequest request){
        Ticket ticket = ticketService.createTicket(request.message());
        long ticketId = ticket.getId().orElseThrow(() -> new IllegalStateException("ticket id cannot be null"));

        ChatSession session = chatSessionService.createSession(ticketId);


        CreateTicketResponse response = new CreateTicketResponse(
                ticket.getId().orElseThrow(
                        () -> new IllegalStateException("ticket id cannot be null")
                ),
                session.getSessionToken(), //sessionToken
                null //queuePosition
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
