package com.livedesk.agent.domain;

import com.livedesk.agent.PasswordHasher;
import jakarta.persistence.*;

import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "agents")
public class Agent {
    @Id
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private Integer maxConcurrency = 3;

    @Column(nullable = false)
    private boolean isOnline = false;

    @Column(nullable = false)
    private Integer activeChatCount = 0;

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

    public UUID getId(){
        return id;
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
    public Integer getMaxConcurrency(){
        return maxConcurrency;
    }
    public Integer getActiveChatCount(){
        return activeChatCount;
    }
    public Boolean isOnline(){
        return isOnline;
    }

    public void goOnline() {
        isOnline = true;
    }
    public void goOffline() {
        isOnline = false;
    }

    public void incrementActiveChatCount() {
        if(activeChatCount >= maxConcurrency){
            throw new IllegalStateException("Agent has already been assigned maximum tickets");
        }
        this.activeChatCount += 1;
    }

    public void decrementActiveChatCount() {
        if(activeChatCount <= 0)
            throw new IllegalStateException("Agent already has no assigned ticket");
        this.activeChatCount -= 1;
    }
}
