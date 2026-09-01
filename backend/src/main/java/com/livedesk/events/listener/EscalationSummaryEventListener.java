package com.livedesk.events.listener;

import com.livedesk.agent.domain.Agent;
import com.livedesk.agent.service.AdminPresenceService;
import com.livedesk.events.dto.EscalationSummaryEvent;
import com.livedesk.events.email.EmailService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.io.IOException;
import java.util.List;

@Component
public class EscalationSummaryEventListener {

    private final AdminPresenceService adminPresenceService;
    private final EmailService emailService;

    public EscalationSummaryEventListener(
            AdminPresenceService adminPresenceService,
            EmailService emailService
    ) {
        this.adminPresenceService = adminPresenceService;
        this.emailService = emailService;
    }

    @TransactionalEventListener
    public void handleEscalationSummary(EscalationSummaryEvent event) {

        if (adminPresenceService.hasOnlineAdmin()) {
            return;
        }

        List<Agent> recipients =
                adminPresenceService.getEmailRecipients();

        for (Agent admin : recipients) {
            try {
                emailService.sendEscalationSummary(
                        admin.getEmail(),
                        (int) event.queuedEscalatedCount(),
                        (int) event.unansweredEscalatedCount()
                );
            } catch (IOException e) {
                System.out.println("Email Not Sent to admin : "+admin.getEmail());
            }
        }
    }
}