package com.livedesk.agent.controller;

import com.livedesk.agent.dto.AgentPrincipal;
import com.livedesk.agent.service.AgentPresenceService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;

public class AgentPresenceController {

    private final AgentPresenceService agentPresenceService;

    public AgentPresenceController(AgentPresenceService agentPresenceService){
        this.agentPresenceService = agentPresenceService;
    }
    @MessageMapping("/agent/heartbeat")
    public void handleHeartbeat(Authentication authentication, StompHeaderAccessor accessor){
        if(authentication.getPrincipal() instanceof AgentPrincipal agentPrincipal){
            agentPresenceService.refresh(agentPrincipal.agentId(), accessor.getSessionId());
        }
    }
}
