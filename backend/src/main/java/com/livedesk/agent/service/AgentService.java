package com.livedesk.agent.service;

import com.livedesk.agent.repository.AgentRepository;
import com.livedesk.agent.PasswordHasher;
import com.livedesk.agent.domain.Role;
import com.livedesk.agent.domain.Agent;
import com.livedesk.agent.dto.LoginAgentRequest;
import com.livedesk.agent.dto.LoginAgentResponse;
import com.livedesk.agent.exception.DuplicateEmailException;
import com.livedesk.agent.exception.InvalidCredentialsException;
import com.livedesk.auth.jwt.JwtService;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.UUID;

@Service
public class AgentService {
    private final AgentRepository agentRepository;
    private final PasswordHasher passwordHasher;
    private final JwtService jwtService;
    //Constructor Injection
    public AgentService(AgentRepository agentRepository, PasswordHasher passwordHasher, JwtService jwtService){
        this.agentRepository = agentRepository;
        this.passwordHasher = passwordHasher;
        this.jwtService = jwtService;
    }
    public Agent createAgentAccount(String email, String rawPassword){
        // New Agent created
        return  createAgent(
                email,
                rawPassword,
                Role.AGENT
        );
    }

    public LoginAgentResponse login(LoginAgentRequest loginAgentRequest) {
        String email = loginAgentRequest.email();
        String rawPassword = loginAgentRequest.rawPassword();

        Agent agent = agentRepository.findByEmail(email)
                .orElseThrow(() ->
                        new InvalidCredentialsException("Invalid Credentials"));

        if (!agent.matchesPassword(rawPassword, passwordHasher)) {
            throw new InvalidCredentialsException("Invalid Credentials");
        }
        UUID id = agent.getId()
                .orElseThrow(() ->
                        new IllegalStateException("Authenticated agent has no id"));

        return new LoginAgentResponse(id, agent.getEmail(), jwtService.generateToken(id, agent.getEmail(), agent.getRole()));
    }
    public Agent createAgent(String email, String rawPassword, Role role) {

        email = email.toLowerCase(Locale.ROOT).trim();

        if(agentRepository.findByEmail(email).isPresent()){
            throw new DuplicateEmailException("Email already exists.");
        }

        String hashedPassword = passwordHasher.hash(rawPassword);

        Agent agent = new Agent(email, hashedPassword, role);

        return agentRepository.save(agent);
    }
}
