package com.livedesk.events.service;

import com.livedesk.events.dto.TicketAssignedAgentNotification;
import com.livedesk.events.dto.TicketAssignedCustomerNotification;
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

    public void notifyCustomer(UUID ticketId, TicketAssignedCustomerNotification notification){
        messagingTemplate.convertAndSend(
                WebSocketDestinations.ticketNotifications(ticketId),
                notification
        );
    }

    public void notifyAgent(UUID agentId, TicketAssignedAgentNotification notification){
        messagingTemplate.convertAndSendToUser(
                agentId.toString(),
                WebSocketDestinations.AGENT_NOTIFICATIONS,
                notification
        );
    }

    public void notifyAdmins(TicketAssignedCustomerNotification notification){
        messagingTemplate.convertAndSend(
                WebSocketDestinations.ADMIN_NOTIFICATIONS,
                notification
        );
    }
}
