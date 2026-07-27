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
        String email = agentRequest.email().toLowerCase(Locale.ROOT); // normalized email
        String rawPassword = agentRequest.rawPassword();

        //Check weather an email already exists
        if(agentRepository.findByEmail(email).isPresent()){
            throw new DuplicateEmailException("Email already exists.");
        }

        //hash raw password
        String hashedPassword = passwordHasher.hash(rawPassword);
        Agent agent = new Agent(email, hashedPassword, Role.AGENT); // New Agent created

        Long agentId = agentRepository.save(agent)
                .getId()
                .orElseThrow(() ->
                        new IllegalStateException("Saved agent has no ID"));
        return new RegisterAgentResponse(agentId, email, jwtService.generateToken(agentId, email, Role.AGENT));
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
}
