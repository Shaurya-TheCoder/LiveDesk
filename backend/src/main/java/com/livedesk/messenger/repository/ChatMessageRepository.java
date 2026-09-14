package com.livedesk.messenger.repository;

import com.livedesk.messenger.domain.ChatMessage;
import com.livedesk.messenger.domain.MessageSender;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {
    Page<ChatMessage> findByTicketIdOrderByCreatedAtAsc(UUID ticketId, Pageable pageable);

    Optional<ChatMessage> findTopByTicketIdAndSenderOrderByCreatedAtDesc(
            UUID ticketId,
            MessageSender sender
    );
}
