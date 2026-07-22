package com.livedesk.agent;

import org.springframework.stereotype.Repository;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


@Repository
public class AgentRepository {
    private final AtomicLong idGenerator = new AtomicLong(0);
    private final Map<Long, Agent> agentsById = new ConcurrentHashMap<>();

    public Agent save(Agent agent){
        long newId = idGenerator.incrementAndGet();
        agent.setId(newId);
        agentsById.put(newId , agent);

        return agent;
    }

    public Optional<Agent> findById(Long id){
        return Optional.ofNullable(agentsById.get(id));
    }
    public Optional<Agent> findByEmail(String email) {
        Objects.requireNonNull(email, "email must not be null");

        String normalizedEmail = email.toLowerCase(Locale.ROOT);

        return agentsById.values()
                .stream()
                .filter(agent -> agent.getEmail().equals(normalizedEmail))
                .findFirst();
    }
}