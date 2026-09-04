package com.livedesk.ticket.service;

import com.livedesk.ticket.domain.Ticket;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class TicketQueueProcessingService {

    private final RoutingService routingService;

    public TicketQueueProcessingService(RoutingService routingService) {
        this.routingService = routingService;
    }

    @Async
    public void processQueuedTicketsAsync() {
        routingService.assignQueuedTickets();
    }
}
