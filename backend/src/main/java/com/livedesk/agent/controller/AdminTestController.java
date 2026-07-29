package com.livedesk.agent.controller;

import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class AdminTestController {

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/admin/test")
    public ResponseEntity<?> getCurrentAgent(Authentication authentication){
        String id = authentication.getName();

        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return ResponseEntity.ok(Map.of(
                "id", id,
                "roles", roles
        ));
    }
}
