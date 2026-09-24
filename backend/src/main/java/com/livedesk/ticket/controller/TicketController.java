package com.livedesk.ticket.controller;


import com.livedesk.agent.dto.AgentIsOnline;
import com.livedesk.agent.dto.AgentPrincipal;
import com.livedesk.agent.service.AgentPresenceService;
import com.livedesk.auth.service.TicketAuthorizationService;
import com.livedesk.auth.session_token.CustomerPrincipal;
import com.livedesk.chatsession.domain.ChatSession;
import com.livedesk.chatsession.service.ChatSessionService;
import com.livedesk.messenger.dto.ChatMessageResponse;
import com.livedesk.messenger.dto.PageResponse;
import com.livedesk.messenger.service.ChatMessageService;
import com.livedesk.ticket.RecoveryCodeGenerator;
import com.livedesk.ticket.dto.*;
import com.livedesk.ticket.service.TicketService;
import com.livedesk.ticket.domain.Ticket;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1")
public class TicketController {
    private final TicketService ticketService;
    private final ChatSessionService chatSessionService;
    private final ChatMessageService chatMessageService;
    private final TicketAuthorizationService ticketAuthorizationService;
    private final AgentPresenceService agentPresenceService;

    public TicketController(AgentPresenceService agentPresenceService, ChatMessageService chatMessageService, TicketService ticketService,TicketAuthorizationService ticketAuthorizationService, ChatSessionService chatSessionService){
        this.ticketService = ticketService;
        this.ticketAuthorizationService = ticketAuthorizationService;
        this.chatSessionService = chatSessionService;
        this.chatMessageService = chatMessageService;
        this.agentPresenceService = agentPresenceService;
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
        String recoveryCode = RecoveryCodeGenerator.generate();
        Ticket ticket = ticketService.createTicket(request.message(), LocalDateTime.now(), request.subject(), recoveryCode);
        UUID ticketId = ticket.getId();

        ChatSession session = chatSessionService.createSession(ticketId);


        CreateTicketResponse response = new CreateTicketResponse(
                ticket.getId(),
                session.getSessionToken(), //sessionToken
                ticketService.getQueuePosition(ticket),//queuePosition
                recoveryCode
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/tickets/{id}/resolve")
    public ResponseEntity<ResolveTicketResponse> resolveTicket(@PathVariable UUID id, Authentication authentication){
        ticketAuthorizationService.verifyAssignedAgent(id, authentication);
        ticketService.resolveTicket(id);

        ResolveTicketResponse response = new ResolveTicketResponse(
                "Ticket Resolved Successfully"
        );

        return ResponseEntity.ok(response);
    }
    @GetMapping("/tickets/{ticketId}/messages")
    public ResponseEntity<PageResponse<ChatMessageResponse>> getMessages(
            @PathVariable UUID ticketId,
            Authentication authentication,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                chatMessageService.getMessages(
                        ticketId,
                        authentication,
                        pageable
                )
        );
    }

    @GetMapping("/tickets/{ticketId}/agent-presence")
    public ResponseEntity<AgentIsOnline> isAgentOnline(@PathVariable UUID ticketId) {
        Ticket ticket = ticketService.getTicket(ticketId);

        UUID agentId = ticket.getAssignedAgentId();

        if (agentId == null || !agentPresenceService.isOnline(agentId)) {
            return ResponseEntity.ok(new AgentIsOnline(false));
        }

        return ResponseEntity.ok(new AgentIsOnline(true));
    }

    @GetMapping("/tickets/assigned")
    public ResponseEntity<List<AgentTicketResponse>> getAgentAssignedTickets(
            Authentication authentication
    ) {
        if (authentication.getPrincipal() instanceof AgentPrincipal agentPrincipal) {

            List<AgentTicketResponse> assignedTickets =
                    ticketService.getAgentAssignedTickets(agentPrincipal.agentId());

            return ResponseEntity.ok(assignedTickets);
        }

        throw new AccessDeniedException("Only agents can access assigned tickets");
    }

    @GetMapping("/tickets/resolved")
    public ResponseEntity<List<AgentTicketResponse>> getAgentResolvedTickets(
            Authentication authentication
    ) {
        if (authentication.getPrincipal() instanceof AgentPrincipal agentPrincipal) {

            List<AgentTicketResponse> resolvedTickets =
                    ticketService.getAgentResolvedTickets(agentPrincipal.agentId());

            return ResponseEntity.ok(resolvedTickets);
        }

        throw new AccessDeniedException("Only agents can access resolved tickets");
    }

    @PostMapping("/tickets/recover")
    public ResponseEntity<RecoverTicketResponse> recoverTicket(
            @Valid @RequestBody RecoverTicketRequest request) {

        ChatSession session = ticketService.recoverTicket(
                request.recoveryCode()
        );

        RecoverTicketResponse response = new RecoverTicketResponse(
                session.getTicketId(),
                session.getSessionToken()
        );

        return ResponseEntity.ok(response);
    }
}
