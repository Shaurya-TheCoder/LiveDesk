package com.livedesk.agent;

import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

@Service
public class AgentService {
    private final AgentRepository agentRepository;
    private final PasswordHasher passwordHasher;

    //Constructor Injection
    public AgentService(AgentRepository agentRepository, PasswordHasher passwordHasher){
        this.agentRepository = agentRepository;
        this.passwordHasher = passwordHasher;
    }
    public RegisterAgentResponse register(RegisterAgentRequest agentRequest){
        String email = agentRequest.email().toLowerCase(Locale.ROOT); // normalized email
        String rawPassword = agentRequest.rawPassword();

        //Check weather an email already exists
        if(agentRepository.findByEmail(email).isPresent()){
            throw new DuplicateEmailException("Email already exists.");
        }

        //hash raw password
        String hashedPassword = passwordHasher.hash(rawPassword);
        Agent agent = new Agent(email, hashedPassword); // New Agent created

        Long agentId = agentRepository.save(agent)
                .getId()
                .orElseThrow(() ->
                        new IllegalStateException("Saved agent has no ID"));
        return new RegisterAgentResponse(agentId, email);
    }
}
