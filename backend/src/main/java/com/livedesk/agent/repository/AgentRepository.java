package com.livedesk.agent.repository;

import com.livedesk.agent.domain.Agent;
import com.livedesk.agent.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface AgentRepository extends JpaRepository<Agent, UUID> {
    Optional<Agent> findByEmail(String email);

    @Query(
            value = """
        SELECT *
        FROM agents
        WHERE role = 'AGENT'
        AND is_online = true
        AND active_chat_count < max_concurrency
        ORDER BY active_chat_count ASC
        LIMIT 1
        FOR UPDATE
        """,
            nativeQuery = true
    )
    Optional<Agent> findAvailableAgentForUpdate();

    @Query(value = """
    SELECT id
    FROM agents
    WHERE role = 'AGENT'
    AND active_chat_count < max_concurrency
    ORDER BY active_chat_count ASC
    LIMIT 10
    """, nativeQuery = true)
    List<UUID> findCapacityAvailableAgentIds();

    @Query(value = """
    SELECT *
    FROM agents
    WHERE id = :agentId
    AND active_chat_count < max_concurrency
    FOR UPDATE
    """, nativeQuery = true)
    Optional<Agent> lockAgentIfAvailable(@Param("agentId") UUID agentId);

    List<Agent> findByRole(Role role);
}