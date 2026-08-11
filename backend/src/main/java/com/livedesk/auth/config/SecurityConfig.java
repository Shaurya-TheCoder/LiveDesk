package com.livedesk.auth.config;

import com.livedesk.auth.RestAccessDeniedHandler;
import com.livedesk.auth.RestAuthenticationEntryPoint;
import com.livedesk.auth.service.TokenAuthenticationService;
import com.livedesk.auth.jwt.JwtAuthenticationFilter;
import com.livedesk.auth.session_token.SessionTokenAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(
            TokenAuthenticationService tokenAuthenticationService
    ) {
        return new JwtAuthenticationFilter(tokenAuthenticationService);
    }

    @Bean
    public SessionTokenAuthenticationFilter sessionTokenAuthenticationFilter(
            TokenAuthenticationService tokenAuthenticationService
    ) {
        return new SessionTokenAuthenticationFilter (tokenAuthenticationService);
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter,
                                                   RestAuthenticationEntryPoint restAuthenticationEntryPoint,
                                                   SessionTokenAuthenticationFilter sessionTokenAuthenticationFilter, RestAccessDeniedHandler restAccessDeniedHandler){
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/v1/tickets").permitAll()
                        .requestMatchers("/ws/**").permitAll()
                        .anyRequest().authenticated()
                ).addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(sessionTokenAuthenticationFilter, JwtAuthenticationFilter.class)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(restAuthenticationEntryPoint)
                        .accessDeniedHandler(restAccessDeniedHandler)
                );
        return http.build();
    }
}
