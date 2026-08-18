package com.livedesk.messenger.websocket;

import java.util.UUID;

public final class WebSocketDestinations {

    private WebSocketDestinations() {}

    public static String ticketNotifications(UUID ticketId) {
        return "/topic/ticket/" + ticketId + "/notifications";
    }

    public static final String AGENT_NOTIFICATIONS =
            "/queue/notifications";

    public static final String ADMIN_NOTIFICATIONS =
            "/topic/admin/notifications";

    public static final String QUEUE =
            "/topic/queue";
}