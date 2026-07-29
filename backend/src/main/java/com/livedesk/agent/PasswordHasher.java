package com.livedesk.agent;

public interface PasswordHasher {
    String hash(String rawPassword);
    Boolean matches(String rawPassword, String passwordHash);
}

