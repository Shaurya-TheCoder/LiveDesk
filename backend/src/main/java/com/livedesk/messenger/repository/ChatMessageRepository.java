package com.livedesk.messenger.repository;

import com.livedesk.messenger.domain.ChatMessage;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ChatMessageRepository {
    private final AtomicLong idGenerator = new AtomicLong(0);
    private final Map<Long, ChatMessage> chatStore = new ConcurrentHashMap<>();

    public ChatMessage save(ChatMessage message){
        if(message.getId().isEmpty()){
            message.setId(idGenerator.incrementAndGet());
        }

        chatStore.put(message.getId().orElseThrow(
                () -> new IllegalStateException("message id wasn't found")
        ), message);

        return message;
    }

    public Optional<ChatMessage> findById(Long id) {
        return Optional.ofNullable(chatStore.get(id));
    }

}
