package com.livedesk.events.service;

import com.livedesk.events.dto.*;
import com.livedesk.messenger.websocket.WebSocketDestinations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class NotificationService {
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyAgentAssignedToCustomer(UUID ticketId, UUID agentId){
        TicketAssignedCustomerNotification notification = new TicketAssignedCustomerNotification(
                "TICKET_ASSIGNED",
                agentId,
                "An agent has been assigned to help you out. Be patient!"
        );

        messagingTemplate.convertAndSend(
                WebSocketDestinations.ticketNotifications(ticketId),
                notification
        );
    }

    public void notifyTicketAssignedToAgent(UUID ticketId, UUID agentId){
        TicketAssignedAgentNotification notification = new TicketAssignedAgentNotification(
                "TICKET_ASSIGNED",
                ticketId,
                "A new request has been assigned to you. Resolve it ASAP."
        );
        messagingTemplate.convertAndSendToUser(
                agentId.toString(),
                WebSocketDestinations.AGENT_NOTIFICATIONS,
                notification
        );

    }

    public void notifyTicketEscalatedToAdmins(UUID ticketId, UUID agentId){

        String message;
        if(agentId != null)
            message = "Ticket Id: "+ticketId+" assigned to agent: "+ticketId+" has been escalated!";
        else
            message = ticketId + " hasn't been assigned an agent";

        TicketEscalatedAdminNotification notification= new TicketEscalatedAdminNotification(
                "TICKET_ESCALATED",
                ticketId,
                message
        );
        messagingTemplate.convertAndSend(
                WebSocketDestinations.ADMIN_NOTIFICATIONS,
                notification
        );
    }

    public void notifyTicketEscalatedToAgent(UUID ticketId, UUID agentId){
        TicketEscalatedAgentNotification notification = new TicketEscalatedAgentNotification(
                "TICKET_ESCALATED",
                ticketId,
                ticketId+" has been escalated. Respond ASAP!"
            );

        messagingTemplate.convertAndSendToUser(
                agentId.toString(),
                WebSocketDestinations.AGENT_NOTIFICATIONS,
                notification
        );
    }
}
