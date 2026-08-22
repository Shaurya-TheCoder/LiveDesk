package com.livedesk.agent.dto;

import com.livedesk.agent.domain.Role;

import java.security.Principal;
import java.util.UUID;

public record AgentPrincipal(
        UUID agentId,
        String email,
        Role role
) implements Principal {

    @Override
    public String getName() {
        return agentId.toString();
    }
}