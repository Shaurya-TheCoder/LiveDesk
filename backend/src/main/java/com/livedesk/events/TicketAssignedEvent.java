package com.livedesk.events;

import java.util.UUID;

public record TicketAssignedEvent(UUID ticketId, UUID agentId) {}
