package com.livedesk.ticket.controller;

import com.livedesk.auth.service.TicketAuthorizationService;
import com.livedesk.auth.session_token.CustomerPrincipal;
import com.livedesk.chatsession.domain.ChatSession;
import com.livedesk.chatsession.service.ChatSessionService;
import com.livedesk.ticket.service.TicketService;
import com.livedesk.ticket.domain.Ticket;
import com.livedesk.ticket.dto.CreateTicketRequest;
import com.livedesk.ticket.dto.CreateTicketResponse;
import com.livedesk.ticket.dto.GetTicketResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1")
public class TicketController {
    private final TicketService ticketService;
    private final ChatSessionService chatSessionService;
    private final TicketAuthorizationService ticketAuthorizationService;

    public TicketController(TicketService ticketService,TicketAuthorizationService ticketAuthorizationService, ChatSessionService chatSessionService){
        this.ticketService = ticketService;
        this.ticketAuthorizationService = ticketAuthorizationService;
        this.chatSessionService = chatSessionService;
    }
    @GetMapping("/tickets/{id}")
    public ResponseEntity<GetTicketResponse> getTicket(
            @PathVariable UUID id,
            Authentication authentication) {

        Ticket ticket = ticketService.getTicket(id);

        if (authentication.getPrincipal() instanceof CustomerPrincipal principal) {
            ticketAuthorizationService.verifyCustomerAccess(ticket, principal);
        }

        GetTicketResponse response = new GetTicketResponse(
                ticket.getId(),
                ticket.getStatus(),
                ticket.getCreatedAt()
        );

        return ResponseEntity.ok(response);
    }
    @PostMapping("/tickets")
    public ResponseEntity<CreateTicketResponse> createTicket(@Valid @RequestBody CreateTicketRequest request){
        Ticket ticket = ticketService.createTicket(request.message(), LocalDateTime.now());
        UUID ticketId = ticket.getId();

        ChatSession session = chatSessionService.createSession(ticketId);


        CreateTicketResponse response = new CreateTicketResponse(
                ticket.getId(),
                session.getSessionToken(), //sessionToken
                null //queuePosition
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
