package com.livedesk.auth.session_token;

import com.livedesk.auth.RestAuthenticationEntryPoint;
import com.livedesk.chatsession.ChatSession;
import com.livedesk.chatsession.ChatSessionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class SessionTokenAuthenticationFilter extends OncePerRequestFilter {
    private final ChatSessionService chatSessionService;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    public SessionTokenAuthenticationFilter(ChatSessionService chatSessionService, RestAuthenticationEntryPoint restAuthenticationEntryPoint){
        this.chatSessionService = chatSessionService;
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
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
            ChatSession session = chatSessionService.validateToken(token);

            CustomerPrincipal principal = new CustomerPrincipal(session.getTicketId());

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    principal,
                    null,
                    Collections.emptyList()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }catch(InvalidSessionTokenException exception){
            restAuthenticationEntryPoint.commence(request, response, exception);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
