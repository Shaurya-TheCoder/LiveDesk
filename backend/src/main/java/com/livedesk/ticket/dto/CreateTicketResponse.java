package com.livedesk.ticket.dto;

import java.util.UUID;

public record CreateTicketResponse(
        UUID ticketId,
        String sessionToken,
        Long queuePosition
) {}
