package com.livedesk.agent;

import com.livedesk.agent.constant.Role;
import com.livedesk.agent.dto.LoginAgentRequest;
import com.livedesk.agent.dto.LoginAgentResponse;
import com.livedesk.agent.dto.RegisterAgentRequest;
import com.livedesk.agent.dto.RegisterAgentResponse;
import com.livedesk.agent.exception.DuplicateEmailException;
import com.livedesk.agent.exception.InvalidCredentialsException;
import com.livedesk.auth.jwt.JwtService;
import org.springframework.stereotype.Service;

import java.util.Locale;

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
    public RegisterAgentResponse register(RegisterAgentRequest agentRequest){
        // New Agent created
        Agent agent = createAgent(
                agentRequest.email(),
                agentRequest.rawPassword(),
                Role.AGENT
        );
        Long agentId = agent
                .getId()
                .orElseThrow(() ->
                        new IllegalStateException("Saved agent has no ID"));
        return new RegisterAgentResponse(agentId, agent.getEmail(), jwtService.generateToken(agentId, agent.getEmail(), Role.AGENT));
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
        Long id = agent.getId()
                .orElseThrow(() ->
                        new IllegalStateException("Authenticated agent has no id"));

        return new LoginAgentResponse(id, agent.getEmail(), jwtService.generateToken(id, agent.getEmail(), agent.getRole()));
    }
    Agent createAgent(String email, String rawPassword, Role role) {

        email = email.toLowerCase(Locale.ROOT).trim();

        if(agentRepository.findByEmail(email).isPresent()){
            throw new DuplicateEmailException("Email already exists.");
        }

        String hashedPassword = passwordHasher.hash(rawPassword);

        Agent agent = new Agent(email, hashedPassword, role);

        return agentRepository.save(agent);
    }
}
