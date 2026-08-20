package com.livedesk.events.dto;

import java.util.UUID;

public record TicketEscalatedEvent(UUID ticketId, UUID agentId) {
}
