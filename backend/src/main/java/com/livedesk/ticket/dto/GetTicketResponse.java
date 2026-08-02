package com.livedesk.ticket.dto;

import com.livedesk.ticket.TicketStatus;

import java.time.LocalDateTime;

public record GetTicketResponse(
        long ticketId,
        TicketStatus status,
        LocalDateTime createdAt
) {}
