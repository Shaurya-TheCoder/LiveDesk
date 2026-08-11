package com.livedesk.auth.jwt;

import com.livedesk.auth.AuthErrorResponseWriter;
import com.livedesk.auth.service.TokenAuthenticationService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;


public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final TokenAuthenticationService tokenAuthenticationService;

    public JwtAuthenticationFilter(TokenAuthenticationService tokenAuthenticationService){
        this.tokenAuthenticationService = tokenAuthenticationService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7); //Extract the token from the header

        try{
            Authentication authToken = tokenAuthenticationService.authenticateJwt(token);
            SecurityContextHolder.getContext().setAuthentication(authToken);

        } catch (JwtException e) {
            logger.warn("Invalid JWT: ", e);
            AuthErrorResponseWriter.writeUnauthorized(response);
            return;
        } catch (IllegalArgumentException e) {
            logger.warn("Unknown role in JWT", e);
            AuthErrorResponseWriter.writeUnauthorized(response);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
