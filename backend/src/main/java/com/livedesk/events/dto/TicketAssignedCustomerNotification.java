package com.livedesk.events.dto;

import java.util.UUID;

public record TicketAssignedCustomerNotification(String type, UUID ticketId, String message) {}
