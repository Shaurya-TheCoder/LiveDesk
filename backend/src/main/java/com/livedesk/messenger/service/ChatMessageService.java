package com.livedesk.messenger.service;

import com.livedesk.auth.TicketAuthorizationService;
import com.livedesk.auth.session_token.CustomerPrincipal;
import com.livedesk.messenger.domain.ChatMessage;
import com.livedesk.messenger.domain.MessageSender;
import com.livedesk.messenger.repository.ChatMessageRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final TicketAuthorizationService ticketAuthorizationService;

    public ChatMessageService(ChatMessageRepository chatMessageRepository, TicketAuthorizationService ticketAuthorizationService) {
        this.chatMessageRepository = chatMessageRepository;
        this.ticketAuthorizationService = ticketAuthorizationService;
    }
    public ChatMessage sendMessage(Long ticketId, String content, Authentication authentication){
        ticketAuthorizationService.verifyAccess(ticketId, authentication);

        MessageSender sender = authentication.getPrincipal() instanceof CustomerPrincipal
                ? MessageSender.CUSTOMER
                : MessageSender.AGENT;

        ChatMessage message = new ChatMessage(ticketId, sender, content, LocalDateTime.now());

        return chatMessageRepository.save(message);
    }
}
