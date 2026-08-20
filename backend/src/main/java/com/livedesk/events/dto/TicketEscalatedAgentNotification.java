package com.livedesk.events.dto;

import java.util.UUID;

public record TicketEscalatedAgentNotification (
        String type,
        UUID ticketId,
        String message
) {
}
