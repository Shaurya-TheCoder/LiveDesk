package com.livedesk.events.listener;

import com.livedesk.events.dto.*;
import com.livedesk.events.service.NotificationService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;


@Component
public class TicketEscalatedEventListener {
    private final NotificationService notificationService;

    public TicketEscalatedEventListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @TransactionalEventListener
    public void handleNotification(TicketEscalatedEvent event){
        System.out.println("listner listening...");
        if(event.agentId() != null)
            notificationService.notifyTicketEscalatedToAgent(event.ticketId(), event.agentId());
        notificationService.notifyTicketEscalatedToAdmins(event.ticketId(), event.agentId());
    }
}