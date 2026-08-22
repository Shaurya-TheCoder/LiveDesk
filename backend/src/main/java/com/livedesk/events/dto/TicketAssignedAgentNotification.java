package com.livedesk.events.dto;

import java.util.UUID;

public record TicketAssignedAgentNotification(String type, UUID agentId, String message) {}
