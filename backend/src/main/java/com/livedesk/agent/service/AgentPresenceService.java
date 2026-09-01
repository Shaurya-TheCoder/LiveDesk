package com.livedesk.agent.service;

import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

@Component
public class AgentPresenceService {
    private static final Duration PRESENCE_TLL = Duration.ofSeconds(30);
    private static final String PRESENCE_KEY_PREFIX = "agent:presence:";

    private final RedisTemplate<String, String> redisTemplate;

    public AgentPresenceService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void markOnline(UUID agentId, String sessionId){
        String key = PRESENCE_KEY_PREFIX+agentId+":"+sessionId;

        redisTemplate.opsForValue().set(key, "ONLINE", PRESENCE_TLL);
    }

    public void refresh(UUID agentId, String sessionId){
        String key = PRESENCE_KEY_PREFIX+agentId+":"+sessionId;
        redisTemplate.expire(key, PRESENCE_TLL);
    }

    public void markOffline(UUID agentId, String sessionId){
        String key = PRESENCE_KEY_PREFIX+agentId+":"+sessionId;
        redisTemplate.delete(key);
    }
    public boolean isOnline(UUID agentId) {
        String pattern = PRESENCE_KEY_PREFIX + agentId + ":*";

        ScanOptions scanOptions = ScanOptions.scanOptions()
                .match(pattern)
                .count(1)
                .build();

        try (Cursor<String> cursor = redisTemplate.scan(scanOptions)) {
            return cursor.hasNext();
        }
    }
}
