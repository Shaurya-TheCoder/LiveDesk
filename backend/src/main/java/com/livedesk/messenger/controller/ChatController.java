package com.livedesk.messenger.controller;

import com.livedesk.messenger.domain.ChatMessage;
import com.livedesk.messenger.domain.MessageSender;
import com.livedesk.messenger.dto.ChatMessageResponse;
import com.livedesk.messenger.dto.SendChatMessageRequest;
import com.livedesk.messenger.service.ChatMessageService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {
    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(ChatMessageService chatMessageService, SimpMessagingTemplate messagingTemplate) {
        this.chatMessageService = chatMessageService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat/{ticketId}")
    public void handleMessage(@DestinationVariable Long ticketId, @Payload SendChatMessageRequest request){
        ChatMessage message = chatMessageService.sendMessage(ticketId, MessageSender.CUSTOMER, request.content());

        ChatMessageResponse response = new ChatMessageResponse(
                message.getId().orElseThrow(),
                message.getTicketId(),
                message.getSender(),
                message.getContent(),
                message.getCreatedAt()
        );
        messagingTemplate.convertAndSend("/topic/chat/" + ticketId, response);
    }
}
