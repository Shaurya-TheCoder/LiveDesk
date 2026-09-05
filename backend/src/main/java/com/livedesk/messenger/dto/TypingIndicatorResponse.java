package com.livedesk.messenger.dto;

import com.livedesk.messenger.domain.MessageSender;

import java.util.UUID;

public record TypingIndicatorResponse(UUID ticketId, MessageSender sender, boolean typing) {
}
