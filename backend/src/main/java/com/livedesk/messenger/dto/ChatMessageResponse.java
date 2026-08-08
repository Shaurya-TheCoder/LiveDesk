package com.livedesk.messenger.dto;

import com.livedesk.messenger.domain.MessageSender;

import java.time.LocalDateTime;

public record ChatMessageResponse(
        Long id,
        Long ticketId,
        MessageSender sender,
        String content,
        LocalDateTime createdAt
){}
