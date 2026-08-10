package com.livedesk.messenger.websocket.interceptor;

import com.livedesk.auth.TicketAuthorizationService;
import com.livedesk.auth.TokenAuthenticationService;
import com.livedesk.auth.session_token.InvalidSessionTokenException;

import io.jsonwebtoken.JwtException;
import org.jspecify.annotations.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

//"SUBSCRIBE-time authorization failures currently terminate the entire STOMP connection
// (default Spring/STOMP behavior on preSend exceptions)
// revisit with a soft-reject (return null + /user/queue/errors push)
// if frontend integration needs the client to survive a denied subscribe without reconnecting."

@Component
public class WebSocketChannelInterceptor implements ChannelInterceptor {

    private final TokenAuthenticationService tokenAuthenticationService;
    private final TicketAuthorizationService ticketAuthorizationService;

    public WebSocketChannelInterceptor(TokenAuthenticationService tokenAuthenticationService, TicketAuthorizationService ticketAuthorizationService) {
        this.tokenAuthenticationService = tokenAuthenticationService;
        this.ticketAuthorizationService = ticketAuthorizationService;
    }

    @Override
    public @Nullable Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(
                        message,
                        StompHeaderAccessor.class
                );

        if (accessor == null) {
            throw new MessagingException(
                    "Unable to obtain STOMP header accessor"
            );
        }

        StompCommand command = accessor.getCommand();

        if(StompCommand.CONNECT == command){
            String authorization = accessor.getFirstNativeHeader("Authorization");
            String sessionToken = accessor.getFirstNativeHeader("Session-Token");

            if(authorization != null){

                if(!authorization.startsWith("Bearer ")){
                    throw new MessagingException("Invalid Authorization Header");
                }
                String token = authorization.substring(7);

                try{
                    Authentication authentication = tokenAuthenticationService.authenticateJwt(token);
                    accessor.setUser(authentication);
                } catch (JwtException | IllegalArgumentException e) {
                    throw new MessagingException("Invalid JWT", e);
                }
            }
            else if(sessionToken != null){
                try {
                    Authentication principal = tokenAuthenticationService.authenticateSessionToken(sessionToken);
                    accessor.setUser(principal);
                } catch (InvalidSessionTokenException e) {
                    throw new MessagingException("Invalid Session Token", e);
                }
            }else{
                throw new MessagingException("Authentication required");
            }
        }else if(StompCommand.SUBSCRIBE == command){
            Authentication authentication = (Authentication) accessor.getUser();

            if(authentication != null){
                String destination = accessor.getDestination();
                if (destination == null) {
                    throw new MessagingException("Missing destination");
                }
                Long ticketId = extractTicketId(destination);
                ticketAuthorizationService.verifyAccess(ticketId, authentication);
                //might return an exception, no exception is handled therefore the connection would break
                //when integrating frontend make this silently reject the request rather than closing the connection
            }else{
                throw new MessagingException("Empty Principal in the accessor");
            }
        }

        return message;
    }

    private Long extractTicketId(String destination) {
        String prefix = "/topic/chat/";

        if (!destination.startsWith(prefix)) {
            throw new MessagingException("Invalid chat destination");
        }

        String ticketIdPart = destination.substring(prefix.length());

        try {
            return Long.parseLong(ticketIdPart);
        } catch (NumberFormatException e) {
            throw new MessagingException("Invalid ticket ID", e);
        }
    }
}
