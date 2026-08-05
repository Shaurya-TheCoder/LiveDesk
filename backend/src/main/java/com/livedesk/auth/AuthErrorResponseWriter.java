package com.livedesk.auth;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

public final class AuthErrorResponseWriter {

    private AuthErrorResponseWriter() {
    }

    public static void write(HttpServletResponse response,
                             int status,
                             String error) throws IOException {

        SecurityContextHolder.clearContext();

        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write("""
                {
                  "error": "%s"
                }
                """.formatted(error));
    }

    public static void writeUnauthorized(HttpServletResponse response)
            throws IOException {

        write(
                response,
                HttpServletResponse.SC_UNAUTHORIZED,
                "Unauthorized"
        );
    }

    public static void writeForbidden(HttpServletResponse response)
            throws IOException {

        write(
                response,
                HttpServletResponse.SC_FORBIDDEN,
                "Forbidden"
        );
    }
}
