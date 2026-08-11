package com.livedesk.agent.domain;

import com.livedesk.agent.PasswordHasher;
import jakarta.persistence.*;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "agents")
public class Agent {
    @Id
    private UUID id;
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String passwordHash;

    protected Agent() {}

    public Agent(String email, String passwordHash, Role role) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email must not be null or blank");
        }
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("passwordHash must not be null or blank");
        }
        if (role == null) {
            throw new IllegalArgumentException("role must not be null");
        }
        this.id = UUID.randomUUID();
        this.email = email.toLowerCase(Locale.ROOT).trim();
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public Optional<UUID> getId(){
        return Optional.ofNullable(id);
    }
    public String getEmail(){
        return email;
    }
    public Role getRole(){ return  role; }

    public void setPasswordHash(String newPasswordHash){
        passwordHash = newPasswordHash;
    }

    public boolean matchesPassword(String rawPassword,
                                   PasswordHasher passwordHasher) {

        Objects.requireNonNull(rawPassword, "rawPassword must not be null");
        Objects.requireNonNull(passwordHasher, "passwordHasher must not be null");

        return passwordHasher.matches(rawPassword, passwordHash);
    }
}
