package com.livedesk.agent.controller;

import com.livedesk.agent.AgentService;
import com.livedesk.agent.dto.LoginAgentRequest;
import com.livedesk.agent.dto.LoginAgentResponse;
import com.livedesk.agent.dto.CreateAgentRequest;
import com.livedesk.agent.dto.CreateAgentResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AgentAuthController {

    private final AgentService agentService;

    public AgentAuthController(AgentService agentService){
        this.agentService = agentService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginAgentResponse> loginAgent(@Valid @RequestBody LoginAgentRequest loginAgentRequest){
        return ResponseEntity.status(HttpStatus.OK)
                .body(agentService.login(loginAgentRequest));
    }
}
