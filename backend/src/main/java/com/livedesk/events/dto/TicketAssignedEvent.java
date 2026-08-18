package com.livedesk.events.dto;

import java.util.UUID;

public record TicketAssignedEvent(UUID ticketId, UUID agentId) {}
