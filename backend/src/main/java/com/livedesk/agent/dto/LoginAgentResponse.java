package com.livedesk.agent.dto;

import com.livedesk.agent.domain.Role;

import java.util.UUID;

public record LoginAgentResponse(UUID id, String email, String token, Role role) {
}
