package com.livedesk.agent;

import com.livedesk.agent.constant.Role;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;


public class Agent {
    private Long id;
    private final String email;
    private final Role role;
    private String passwordHash;

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
        this.email = email.toLowerCase(Locale.ROOT).trim();
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public Optional<Long> getId(){
        return Optional.ofNullable(id);
    }
    void setId(Long id){
        Objects.requireNonNull(id, "Agent id must not be null");
        if(this.id != null){
            throw new IllegalStateException("Agent id cannot be re-assigned.");
        }
        this.id = id;
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
