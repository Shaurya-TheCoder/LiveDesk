package com.livedesk.chatsession;

import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ChatSessionRepository {
    private final AtomicLong idGenerator = new AtomicLong(0);
    private final Map<Long, ChatSession> store = new ConcurrentHashMap<>();
    private final Map<String, Long> tokenIndex = new ConcurrentHashMap<>();

    public ChatSession save(ChatSession session) {
        if (session.getId().isEmpty()) {
            session.setId(idGenerator.incrementAndGet());
        }
        long id = session.getId().orElseThrow(
                () -> new IllegalStateException("chat session id wasn't found.")
        );
        store.put(id, session);
        tokenIndex.put(session.getSessionToken(), id);
        return session;
    }

    public Optional<ChatSession> findByToken(String token) {
        return Optional.ofNullable(tokenIndex.get(token))
                .flatMap(id -> Optional.ofNullable(store.get(id)));
    }
}