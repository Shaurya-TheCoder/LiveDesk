package com.livedesk.agent.dto;

public record AdminSystemStatsResponse(
        long totalCreated,
        long queued,
        long escalated,
        long closed
) {
}
