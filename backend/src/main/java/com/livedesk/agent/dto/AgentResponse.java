package com.livedesk.agent.dto;

import com.livedesk.agent.domain.Role;

import java.util.UUID;

public record AgentResponse(
        UUID id,
        String name,
        String email,
        Role role,
        int activeChatCount
){}
