package com.livedesk.scheduler;

import com.livedesk.ticket.service.EscalationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SLAEscalationJob {
    private static final long SLA_CHECK_INTERVAL_MS = 60000;

    private final EscalationService escalationService;

    public SLAEscalationJob(EscalationService escalationService){
        this.escalationService = escalationService;
    }

    @Scheduled(fixedRate = SLA_CHECK_INTERVAL_MS)
    public void checkEscalations(){
        //logger.info("Running SLA escalation job");
        escalationService.processEscalations();
    }
}
