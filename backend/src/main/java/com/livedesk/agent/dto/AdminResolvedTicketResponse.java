package com.livedesk.agent.dto;

import com.livedesk.ticket.domain.TicketPriority;
import com.livedesk.ticket.domain.TicketStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record AdminResolvedTicketResponse(
        UUID id,
        String subject,
        TicketStatus status,
        TicketPriority priority,
        UUID assignedAgentId,
        LocalDateTime createdAt,
        LocalDateTime lastActivityAt
) {
}