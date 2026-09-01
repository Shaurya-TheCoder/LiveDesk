package com.livedesk.events.dto;

public record EscalationSummaryEvent(
        long queuedEscalatedCount,
        long unansweredEscalatedCount
) {
}