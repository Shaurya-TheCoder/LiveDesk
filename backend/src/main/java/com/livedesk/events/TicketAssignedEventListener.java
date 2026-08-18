package com.livedesk.events;

import com.livedesk.events.dto.TicketAssignedAgentNotification;
import com.livedesk.events.dto.TicketAssignedEvent;
import com.livedesk.events.dto.TicketAssignedCustomerNotification;
import com.livedesk.events.service.NotificationService;
import org.springframework.transaction.event.TransactionalEventListener;

public class TicketAssignedEventListener {
    private final NotificationService notificationService;

    public TicketAssignedEventListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @TransactionalEventListener
    public void handleNotification(TicketAssignedEvent event){

        TicketAssignedCustomerNotification notificationForCustomer = new TicketAssignedCustomerNotification(
                "TICKET_ASSIGNED",
                event.ticketId(),
                "An agent has been assigned to help you out. Be patient!"
        );

        TicketAssignedAgentNotification notificationForAgent = new TicketAssignedAgentNotification(
                "TICKET_ASSIGNED",
                event.agentId(),
                "An agent has been assigned to help you out. Be patient!"
        );

        notificationService.notifyCustomer(event.ticketId(), notificationForCustomer);
        notificationService.notifyAgent(event.agentId(), notificationForAgent);

    }
}
