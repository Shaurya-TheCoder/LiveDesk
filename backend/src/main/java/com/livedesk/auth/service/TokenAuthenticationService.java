package com.livedesk.auth.service;

import com.livedesk.agent.domain.Role;
import com.livedesk.agent.dto.AgentPrincipal;
import com.livedesk.auth.jwt.JwtService;
import com.livedesk.auth.session_token.CustomerPrincipal;
import com.livedesk.chatsession.domain.ChatSession;
import com.livedesk.chatsession.service.ChatSessionService;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class TokenAuthenticationService {

    private final JwtService jwtService;
    private final ChatSessionService chatSessionService;

    public TokenAuthenticationService(JwtService jwtService, ChatSessionService chatSessionService) {
        this.jwtService = jwtService;
        this.chatSessionService = chatSessionService;
    }

    public Authentication authenticateJwt(String token) {
        Claims claims = jwtService.validateToken(token);

        UUID agentId =
                UUID.fromString(
                        claims.getSubject()
                );

        String email =
                claims.get("email", String.class);

        Role role = Role.valueOf(
                claims.get("role", String.class)
        );

        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(
                        "ROLE_" + role.name()
                )
        );
        AgentPrincipal principal = new AgentPrincipal(agentId, email, role);
        return new UsernamePasswordAuthenticationToken(
                principal,
                null,
                authorities
        );
    }
    public Authentication authenticateSessionToken(String token){
        ChatSession session = chatSessionService.validateToken(token);

        CustomerPrincipal principal = new CustomerPrincipal(session.getTicketId());

        return new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        Collections.emptyList()
        );
    }
}