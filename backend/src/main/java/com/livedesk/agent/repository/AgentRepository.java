package com.livedesk.agent.repository;

import com.livedesk.agent.domain.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public interface AgentRepository extends JpaRepository<Agent, UUID> {
    Optional<Agent> findByEmail(String email);
}