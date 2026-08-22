package com.livedesk.events.dto;

import java.util.UUID;

public record TicketEscalatedAdminNotification(
        String type,
        UUID ticketId,
        String message
) {
}
