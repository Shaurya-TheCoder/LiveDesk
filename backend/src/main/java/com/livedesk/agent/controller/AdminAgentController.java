package com.livedesk.agent.controller;


import com.livedesk.agent.Agent;
import com.livedesk.agent.AgentService;
import com.livedesk.agent.dto.CreateAgentRequest;
import com.livedesk.agent.dto.CreateAgentResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/agents")
public class AdminAgentController {

    private final AgentService agentService;

    public AdminAgentController(AgentService agentService){
        this.agentService = agentService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CreateAgentResponse> createAgent(@Valid @RequestBody CreateAgentRequest createAgentRequest){
        Agent agent = agentService.createAgentAccount(createAgentRequest.email(), createAgentRequest.rawPassword());

        CreateAgentResponse response =
                    new CreateAgentResponse(
                            agent.getId().orElseThrow(
                                    () -> new IllegalStateException("Saved agent has no ID")
                            ),
                            agent.getEmail(),
                            agent.getRole()
                    );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }
}
