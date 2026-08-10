package com.livedesk.auth.session_token;

import com.livedesk.auth.TokenAuthenticationService;
import com.livedesk.chatsession.ChatSession;
import com.livedesk.chatsession.ChatSessionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class SessionTokenAuthenticationFilter extends OncePerRequestFilter {
    private final TokenAuthenticationService tokenAuthenticationService;

    public SessionTokenAuthenticationFilter(TokenAuthenticationService tokenAuthenticationService){
        this.tokenAuthenticationService = tokenAuthenticationService;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Session-Token");
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        if(token == null || token.isBlank()){
            filterChain.doFilter(request, response);
            return;
        }
        try {
            Authentication authentication = tokenAuthenticationService.authenticateSessionToken(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }catch(InvalidSessionTokenException exception){
            filterChain.doFilter(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
