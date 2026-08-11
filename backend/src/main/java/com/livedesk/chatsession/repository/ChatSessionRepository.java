package com.livedesk.chatsession.repository;

import com.livedesk.chatsession.domain.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ChatSessionRepository extends JpaRepository<ChatSession, UUID> {

    Optional<ChatSession> findBySessionToken(String sessionToken);
}