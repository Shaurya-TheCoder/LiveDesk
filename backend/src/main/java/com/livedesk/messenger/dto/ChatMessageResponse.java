package com.livedesk.messenger.dto;

import com.livedesk.messenger.domain.MessageSender;

import java.time.LocalDateTime;
import java.util.UUID;

public record ChatMessageResponse(
        UUID id,
        UUID ticketId,
        MessageSender sender,
        String content,
        LocalDateTime createdAt
){}
