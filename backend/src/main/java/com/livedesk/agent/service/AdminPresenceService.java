package com.livedesk.agent.service;

import com.livedesk.agent.domain.Agent;
import com.livedesk.agent.domain.Role;
import com.livedesk.agent.repository.AgentRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
public class AdminPresenceService {
    private final AgentRepository agentRepository;
    private final AgentPresenceService agentPresenceService;

    public AdminPresenceService(AgentRepository agentRepository, AgentPresenceService agentPresenceService){
        this.agentRepository = agentRepository;
        this.agentPresenceService = agentPresenceService;
    }

    public List<UUID> getOnlineAdmins(){
        List<Agent> admins = agentRepository.findByRole(Role.ADMIN);
        List<UUID> onlineAdmins = new ArrayList<>();

        for(Agent admin : admins){
            UUID adminId = admin.getId();
            if(agentPresenceService.isOnline(adminId))
                onlineAdmins.add(adminId);
        }
        return onlineAdmins;
    }

    public boolean hasOnlineAdmin(){
        List<Agent> admins = agentRepository.findByRole(Role.ADMIN);

        for(Agent admin : admins){
            UUID adminId = admin.getId();
            if(agentPresenceService.isOnline(adminId))
                return true;
        }
        return false;
    }
    public List<Agent> getEmailRecipients() {
        List<Agent> admins = new ArrayList<>(
                agentRepository.findByRole(Role.ADMIN)
        );

        Collections.shuffle(admins);

        return admins.subList(
                0,
                Math.min(3, admins.size())
        );
    }
}
