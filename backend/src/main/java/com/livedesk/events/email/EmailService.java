package com.livedesk.events.email;

import java.io.IOException;

public interface EmailService {

    void sendEscalationSummary(
            String recipientEmail,
            int queuedEscalatedCount,
            int unansweredEscalatedCount
    ) throws IOException;
}