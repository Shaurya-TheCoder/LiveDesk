package com.livedesk.agent.controller;

import com.livedesk.agent.dto.AdminResolvedTicketResponse;
import com.livedesk.agent.dto.AdminSystemStatsResponse;
import com.livedesk.agent.dto.AgentDetailsResponse;
import com.livedesk.agent.dto.AgentResponse;
import com.livedesk.agent.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/agents")
    public ResponseEntity<List<AgentResponse>> getAllAgents() {
        return ResponseEntity.ok(adminService.getAllAgents());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/stats")
    public ResponseEntity<AdminSystemStatsResponse> getSystemStats() {
        return ResponseEntity.ok(adminService.getSystemStats());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/agents/{agentId}")
    public ResponseEntity<AgentDetailsResponse> getAgentDetails(
            @PathVariable UUID agentId
    ) {
        return ResponseEntity.ok(
                adminService.getAgentDetails(agentId)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/agents/{agentId}/resolved-tickets")
    public ResponseEntity<List<AdminResolvedTicketResponse>> getAgentResolvedTickets(
            @PathVariable UUID agentId
    ) {
        return ResponseEntity.ok(
                adminService.getAgentResolvedTickets(agentId)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/tickets/{ticketId}/close")
    public ResponseEntity<Void> closeTicket(
            @PathVariable UUID ticketId
    ) {
        adminService.closeTicket(ticketId);
        return ResponseEntity.noContent().build();
    }
}