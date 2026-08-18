package com.livedesk.ticket.repository;

import com.livedesk.ticket.domain.Ticket;
import com.livedesk.ticket.domain.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    @Query(
            value = """
        SELECT *
        FROM tickets
        WHERE status = 'QUEUED'
        ORDER BY created_at ASC
        LIMIT 1
        FOR UPDATE SKIP LOCKED
        """,
            nativeQuery = true
    )
    Optional<Ticket> findOldestQueuedTicketForUpdate();

    @Query(
            value = """
        SELECT COUNT(*) + 1
        FROM tickets
        WHERE status = 'QUEUED'
        AND created_at < :createdAt
        """,
            nativeQuery = true
    )
    long findQueuePosition(
            @Param("createdAt")
            LocalDateTime createdAt
    );

    List<Ticket> findByStatus(TicketStatus status);
}