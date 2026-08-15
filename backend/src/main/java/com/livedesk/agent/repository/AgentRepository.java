package com.livedesk.agent.repository;

import com.livedesk.agent.domain.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.*;

public interface AgentRepository extends JpaRepository<Agent, UUID> {
    Optional<Agent> findByEmail(String email);

    @Query(
            value = """
        SELECT *
        FROM agents
        WHERE is_online = true
        AND active_chat_count < max_concurrency
        ORDER BY active_chat_count ASC
        LIMIT 1
        FOR UPDATE
        """,
            nativeQuery = true
    )
    Optional<Agent> findAvailableAgentForUpdate();
}