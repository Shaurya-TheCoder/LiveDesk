package com.livedesk.agent.dto;

import com.livedesk.agent.domain.Role;

import java.util.UUID;

public record CreateAgentResponse(UUID id, String email, Role role) {
}
