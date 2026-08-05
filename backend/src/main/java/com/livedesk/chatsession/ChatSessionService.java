package com.livedesk.chatsession;

import com.livedesk.auth.session_token.InvalidSessionTokenException;
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
    public ChatSession validateToken(String token){
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Session token must not be blank");
        }

        return chatSessionRepository.findByToken(token)
                .orElseThrow(() -> new InvalidSessionTokenException("Invalid session token"));
    }
}