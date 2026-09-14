package com.livedesk.messenger.service;

import com.livedesk.auth.service.TicketAuthorizationService;
import com.livedesk.auth.session_token.CustomerPrincipal;
import com.livedesk.messenger.domain.ChatMessage;
import com.livedesk.messenger.domain.MessageSender;
import com.livedesk.messenger.dto.ChatMessageResponse;
import com.livedesk.messenger.dto.PageResponse;
import com.livedesk.messenger.dto.TypingIndicatorResponse;
import com.livedesk.messenger.repository.ChatMessageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final TicketAuthorizationService ticketAuthorizationService;

    public ChatMessageService(ChatMessageRepository chatMessageRepository, TicketAuthorizationService ticketAuthorizationService) {
        this.chatMessageRepository = chatMessageRepository;
        this.ticketAuthorizationService = ticketAuthorizationService;
    }
    public ChatMessage sendMessage(UUID ticketId, String content, Authentication authentication){
        ticketAuthorizationService.verifyAccess(ticketId, authentication);

        MessageSender sender = authentication.getPrincipal() instanceof CustomerPrincipal
                ? MessageSender.CUSTOMER
                : MessageSender.AGENT;

        ChatMessage message = new ChatMessage(ticketId, sender, content, LocalDateTime.now());

        return chatMessageRepository.save(message);
    }

    public TypingIndicatorResponse sendTypingResponse(UUID ticketId, boolean isTyping, Authentication authentication){
        ticketAuthorizationService.verifyAccess(ticketId, authentication);

        MessageSender sender = authentication.getPrincipal() instanceof CustomerPrincipal
                ? MessageSender.CUSTOMER
                : MessageSender.AGENT;

        return new TypingIndicatorResponse(
                        ticketId,
                        sender,
                        isTyping
                );
    }
    public PageResponse<ChatMessageResponse> getMessages(UUID ticketId, Authentication authentication, Pageable pageable) {
        ticketAuthorizationService.verifyAccess(ticketId, authentication);

        Page<ChatMessage> messages = chatMessageRepository.findByTicketIdOrderByCreatedAtAsc(ticketId, pageable);

        return new PageResponse<>(
                messages.getContent().stream()
                        .map(message -> new ChatMessageResponse(
                                message.getId().orElseThrow(),
                                message.getTicketId(),
                                message.getSender(),
                                message.getContent(),
                                message.getCreatedAt()
                        )).toList(),
                messages.getNumber(),
                messages.getSize(),
                messages.getTotalElements(),
                messages.getTotalPages(),
                messages.isLast()
        );
    }
}
