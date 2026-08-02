package com.livedesk.ticket.dto;

public record CreateTicketResponse(
        long ticketId,
        String sessionToken,
        Integer queuePosition
) {}
