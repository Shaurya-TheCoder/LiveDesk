package com.livedesk.ticket.dto;

import java.util.UUID;

public record RecoverTicketResponse(
        UUID ticketId,
        String sessionToken
) {}