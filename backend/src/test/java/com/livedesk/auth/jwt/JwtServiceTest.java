package com.livedesk.auth.jwt;

import com.livedesk.agent.domain.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;


@SpringBootTest
class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    @Test
    void shouldGenerateToken(){
        String token = jwtService.generateToken(
                UUID.randomUUID(),
                "test@example.com",
                Role.AGENT
        );

        System.out.println(token);
    }
}

//class JwtServiceTest {
//
//    @Test
//    void shouldGenerateToken() {
//
//        JwtService jwtService = new JwtService();
//
//        ReflectionTestUtils.setField(
//                jwtService,
//                "secretKey",
//                "9d4b8c3a7f21e5d60c91a8ef3b74d2a56fe80c19a4d73eb8f2159c4e87d1ab63f9e62d0c18a7b45ef2d9618cb3475fa1e4d83a90bc76f125ed49a8c61f2b73de"
//        );
//
//        ReflectionTestUtils.setField(
//                jwtService,
//                "expirationMs",
//                900000L
//        );
//
//        String token = jwtService.generateToken(
//                1L,
//                "test@example.com"
//        );
//
//        System.out.println(token);
//    }
//}
//
//
//
//
//





