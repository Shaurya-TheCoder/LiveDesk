package com.livedesk.messenger.websocket.interceptor;

import com.livedesk.auth.service.TicketAuthorizationService;
import com.livedesk.auth.service.TokenAuthenticationService;
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

import java.util.Objects;
import java.util.UUID;

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
                } catch (JwtException e) {
                    throw new MessagingException("Invalid JWT", e);
                } catch (IllegalArgumentException e){
                    throw new MessagingException("Invalid JWT x", e);
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

                if (destination.equals("/user/queue/notifications")) {

                    if (authentication.getAuthorities().stream()
                            .noneMatch(a -> Objects.equals(a.getAuthority(), "ROLE_AGENT"))) {
                        throw new MessagingException("Only agents can subscribe to notifications");
                    }
                }else if(destination.equals("/topic/admin/notifications")){

                    if(authentication.getAuthorities().stream()
                            .noneMatch(a -> Objects.equals(a.getAuthority(), "ROLE_ADMIN"))){
                        throw new MessagingException("Only Admins can subscribe to notifications");
                    }
                }
                else{
                    UUID ticketId = extractTicketId(destination);
                    ticketAuthorizationService.verifyAccess(ticketId, authentication);
                }
            }else{
                throw new MessagingException("Empty Principal in the accessor");
            }
        }
        //might return an exception, no exception is handled therefore the connection would break
        //when integrating frontend make this silently reject the request rather than closing the connection
        return message;
    }

    private UUID extractTicketId(String destination) {

        String ticketIdPart;

        if (destination.startsWith("/topic/chat/")) {
            ticketIdPart = destination.substring("/topic/chat/".length());

        } else if (destination.startsWith("/topic/ticket/")
                && destination.endsWith("/notifications")) {

            ticketIdPart = destination.substring(
                    "/topic/ticket/".length(),
                    destination.length() - "/notifications".length()
            );

        } else {
            throw new MessagingException("Invalid ticket destination");
        }

        try {
            return UUID.fromString(ticketIdPart);
        } catch (IllegalArgumentException e) {
            throw new MessagingException("Invalid ticket ID", e);
        }
    }

}
