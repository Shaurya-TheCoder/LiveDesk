package com.livedesk.ticket.dto;

import com.livedesk.ticket.domain.TicketPriority;
import com.livedesk.ticket.domain.TicketStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record AgentTicketResponse(
        UUID id,
        TicketStatus status,
        TicketPriority priority,
        String subject,
        LocalDateTime createdAt
) {}