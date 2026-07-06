package com.banking.security;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthTokenService {

    private final Map<String, AuthenticatedUser> tokens = new ConcurrentHashMap<>();

    public String createToken(Long userId, Long customerId, String email) {
        String token = UUID.randomUUID().toString();
        tokens.put(token, new AuthenticatedUser(userId, customerId, email));
        return token;
    }

    public Optional<AuthenticatedUser> findByToken(String token) {
        return Optional.ofNullable(tokens.get(token));
    }

    public record AuthenticatedUser(Long userId, Long customerId, String email) {
    }
}
