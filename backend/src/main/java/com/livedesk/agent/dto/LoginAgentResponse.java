package com.livedesk.agent.dto;

import java.util.UUID;

public record LoginAgentResponse(UUID id, String email, String token) {
}
