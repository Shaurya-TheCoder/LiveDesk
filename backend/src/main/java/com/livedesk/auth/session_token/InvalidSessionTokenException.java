package com.livedesk.auth.session_token;

import org.springframework.security.core.AuthenticationException;

public class InvalidSessionTokenException extends AuthenticationException {
    public InvalidSessionTokenException(String message) {
        super(message);
    }
}
