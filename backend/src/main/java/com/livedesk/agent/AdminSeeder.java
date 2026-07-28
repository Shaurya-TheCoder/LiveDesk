package com.livedesk.agent;

import com.livedesk.agent.constant.Role;
import com.livedesk.agent.exception.DuplicateEmailException;
import com.livedesk.auth.jwt.JwtService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements CommandLineRunner{

    private final AgentService agentService;
    private final JwtService jwtService;

    public AdminSeeder(AgentService agentService, JwtService jwtService){
        this.agentService = agentService;
        this.jwtService = jwtService;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            Agent agent = agentService.createAgent(
                    "admin@livedesk.com",
                    "admin123",
                    Role.ADMIN
            );
            Long agentId = agent
                    .getId()
                    .orElseThrow(() ->
                            new IllegalStateException("Saved agent has no ID"));
            System.out.println(jwtService.generateToken(agentId, agent.getEmail(), agent.getRole()));
        }catch (DuplicateEmailException e){
            System.out.println(e.getMessage());
        }
    }
}
