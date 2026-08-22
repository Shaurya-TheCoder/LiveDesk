package com.livedesk.events.listener;

import com.livedesk.events.dto.TicketAssignedAgentNotification;
import com.livedesk.events.dto.TicketAssignedEvent;
import com.livedesk.events.dto.TicketAssignedCustomerNotification;
import com.livedesk.events.service.NotificationService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class TicketAssignedEventListener {
    private final NotificationService notificationService;

    public TicketAssignedEventListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @TransactionalEventListener
    public void handleNotification(TicketAssignedEvent event){
        notificationService.notifyAgentAssignedToCustomer(event.ticketId(), event.agentId());
        notificationService.notifyTicketAssignedToAgent(event.ticketId(), event.agentId());
    }
}
