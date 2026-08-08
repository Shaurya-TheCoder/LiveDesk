package com.livedesk.messenger.service;

import com.livedesk.messenger.domain.ChatMessage;
import com.livedesk.messenger.domain.MessageSender;
import com.livedesk.messenger.repository.ChatMessageRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;

    public ChatMessageService(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }
    public ChatMessage sendMessage(Long ticketId, MessageSender sender, String content){
        ChatMessage message = new ChatMessage(ticketId, sender, content, LocalDateTime.now());

        return chatMessageRepository.save(message);
    }
}
