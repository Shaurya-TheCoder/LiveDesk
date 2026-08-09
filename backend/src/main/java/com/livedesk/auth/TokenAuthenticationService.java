package com.livedesk.auth;

import com.livedesk.agent.constant.Role;
import com.livedesk.auth.jwt.JwtService;
import com.livedesk.auth.session_token.CustomerPrincipal;
import com.livedesk.chatsession.ChatSession;
import com.livedesk.chatsession.ChatSessionService;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

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

        Role role = Role.valueOf(
                claims.get("role", String.class)
        );

        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(
                        "ROLE_" + role.name()
                )
        );

        return new UsernamePasswordAuthenticationToken(
                claims.getSubject(),
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