package com.livedesk.agent;

import com.livedesk.agent.dto.LoginAgentRequest;
import com.livedesk.agent.dto.LoginAgentResponse;
import com.livedesk.agent.dto.RegisterAgentRequest;
import com.livedesk.agent.dto.RegisterAgentResponse;
import com.livedesk.agent.exception.DuplicateEmailException;
import com.livedesk.agent.exception.InvalidCredentialsException;
import org.springframework.stereotype.Service;

import java.util.Locale;

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

    public LoginAgentResponse login(LoginAgentRequest loginAgentRequest){
        String email = loginAgentRequest.email();
        String rawPassword = loginAgentRequest.rawPassword();

        Agent agent = agentRepository.findByEmail(email)
                       .orElseThrow(()->
                               new InvalidCredentialsException("Invalid Credentials"));

        if(!agent.matchesPassword(rawPassword, passwordHasher)){
            throw new InvalidCredentialsException("Invalid Credentials");
        }
        Long id = agent.getId()
                .orElseThrow(() ->
                        new IllegalStateException("Authenticated agent has no id"));

        return new LoginAgentResponse(id , agent.getEmail(), "TODO-generate-jwt");
    }
}
