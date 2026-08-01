package com.livedesk.agent;

import com.livedesk.agent.constant.Role;
import com.livedesk.agent.exception.DuplicateEmailException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements CommandLineRunner{

    private final AgentService agentService;

    private final String email;
    private final String password;

    public AdminSeeder(AgentService agentService, @Value("${admin.email}") String email, @Value("${admin.password}") String password){
        this.agentService = agentService;
        this.email = email;
        this.password = password;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            agentService.createAgent(
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
