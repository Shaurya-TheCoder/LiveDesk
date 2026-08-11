package com.livedesk.messenger.repository;

import com.livedesk.messenger.domain.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {
    Optional<ChatMessage> findByTicketIdOrderByCreatedAtAsc(UUID ticketId);
}
