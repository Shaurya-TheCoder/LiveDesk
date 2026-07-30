package com.livedesk.agent.dto;

import com.livedesk.agent.constant.Role;

public record CreateAgentResponse(Long id, String email, Role role) {
}
