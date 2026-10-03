package com.livedesk.auth.jwt;

import com.livedesk.agent.domain.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Plain JUnit test: no Spring context, no database, no Redis.
 * Same package as JwtService so the package-private init() can be called directly.
 */
class JwtUnitTest {

    private static final String SECRET_A =
            "9d4b8c3a7f21e5d60c91a8ef3b74d2a56fe80c19a4d73eb8f2159c4e87d1ab63";
    private static final String SECRET_B =
            "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef";

    private static JwtService serviceWith(String secret, long expirationMs) throws Exception {
        JwtService service = new JwtService();
        setField(service, "secretKey", secret);
        setField(service, "expirationMs", expirationMs);
        service.init();
        return service;
    }

    private static void setField(Object target, String name, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void tokenRoundTripKeepsSubjectEmailAndRole() throws Exception {
        JwtService service = serviceWith(SECRET_A, 60_000L);
        UUID agentId = UUID.randomUUID();

        String token = service.generateToken(agentId, "asha@example.com", Role.ADMIN);
        Claims claims = service.validateToken(token);

        assertAll(
                () -> assertEquals(agentId.toString(), claims.getSubject()),
                () -> assertEquals("asha@example.com", claims.get("email", String.class)),
                () -> assertEquals("ADMIN", claims.get("role", String.class))
        );
    }

    @Test
    void tokenSignedWithDifferentSecretIsRejected() throws Exception {
        JwtService issuer = serviceWith(SECRET_A, 60_000L);
        JwtService verifier = serviceWith(SECRET_B, 60_000L);

        String token = issuer.generateToken(UUID.randomUUID(), "a@b.com", Role.AGENT);

        assertThrows(JwtException.class, () -> verifier.validateToken(token));
    }

    @Test
    void expiredTokenIsRejected() throws Exception {
        JwtService service = serviceWith(SECRET_A, -1_000L); // already expired when issued

        String token = service.generateToken(UUID.randomUUID(), "a@b.com", Role.AGENT);

        assertThrows(ExpiredJwtException.class, () -> service.validateToken(token));
    }
}