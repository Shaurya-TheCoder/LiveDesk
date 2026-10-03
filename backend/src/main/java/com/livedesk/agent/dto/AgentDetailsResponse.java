package com.livedesk.agent.dto;

import com.livedesk.agent.domain.Role;

import java.util.UUID;

public record AgentDetailsResponse(
        UUID id,
        String name,
        String email,
        Role role,
        boolean online,
        long assignedTicketCount,
        long resolvedTicketCount
) {}