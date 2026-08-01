package com.livedesk.auth.jwt;

import com.livedesk.agent.constant.Role;
import com.livedesk.auth.AuthErrorResponseWriter;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;


public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService){
        this.jwtService = jwtService;
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
            Claims claims = jwtService.validateToken(token);
            String roleClaim = claims.get("role", String.class);
            Role role = Role.valueOf(roleClaim);

            List<GrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority("ROLE_" + role.name())
            );


            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    claims.getSubject(),
                    null,
                    authorities
            );
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
