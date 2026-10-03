package com.livedesk.ticket.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class TicketRateLimiter {

    private static final int MAX_REQUESTS = 2;
    private static final Duration WINDOW = Duration.ofMinutes(2);
    private static final String KEY_PREFIX = "rate-limit:ticket:";

    private final RedisTemplate<String, String> redisTemplate;

    public TicketRateLimiter(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isAllowed(String clientIp) {

        String key = KEY_PREFIX + clientIp;

        Long requestCount = redisTemplate.opsForValue().increment(key);

        if (requestCount == 1) {
            redisTemplate.expire(key, WINDOW);
        }

        return requestCount <= MAX_REQUESTS;
    }
}