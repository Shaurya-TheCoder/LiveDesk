package com.livedesk.chatsession;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class ChatSessionService {
    private static final SecureRandom RANDOM = new SecureRandom();
    private final ChatSessionRepository chatSessionRepository;

    public ChatSessionService(ChatSessionRepository chatSessionRepository) {
        this.chatSessionRepository = chatSessionRepository;
    }

    public ChatSession createSession(Long ticketId) {
        String token = generateToken();
        ChatSession session = new ChatSession(ticketId, token, LocalDateTime.now());
        return chatSessionRepository.save(session);
    }

    private String generateToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}