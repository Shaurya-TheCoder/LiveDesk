package com.livedesk.ticket.dto;

import com.livedesk.ticket.domain.TicketStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record GetTicketResponse(
        UUID ticketId,
        TicketStatus status,
        LocalDateTime createdAt
) {}
