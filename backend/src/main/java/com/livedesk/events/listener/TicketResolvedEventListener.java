package com.livedesk.events.listener;

import com.livedesk.events.dto.TicketResolvedEvent;
import com.livedesk.ticket.service.TicketQueueProcessingService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class TicketResolvedEventListener {
    private final TicketQueueProcessingService queueProcessingService;

    public TicketResolvedEventListener(TicketQueueProcessingService queueProcessingService) {
        this.queueProcessingService = queueProcessingService;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleQueuedTicket(TicketResolvedEvent event){
        queueProcessingService.processQueuedTicketsAsync();
    }
}
