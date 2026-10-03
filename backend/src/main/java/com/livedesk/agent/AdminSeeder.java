package com.livedesk.agent;

import com.livedesk.agent.domain.Role;
import com.livedesk.agent.exception.DuplicateEmailException;
import com.livedesk.agent.service.AgentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements CommandLineRunner{

    private final AgentService agentService;

    private final String name;
    private final String email;
    private final String password;

    public AdminSeeder(AgentService agentService,@Value("${admin.name}") String name,@Value("${admin.email}") String email, @Value("${admin.password}") String password){
        this.agentService = agentService;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            agentService.createAgent(
                    name,
                    email,
                    password,
                    Role.ADMIN
            );
            System.out.println("Admin created successfully");
        }catch (DuplicateEmailException e){
            System.out.println(e.getMessage());
        }
    }
}
