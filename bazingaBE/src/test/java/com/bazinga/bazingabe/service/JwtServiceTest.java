package com.bazinga.bazingabe.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

    private static final String SECRET = "0123456789abcdef0123456789abcdef";
    private static final long EXPIRATION = 60_000L;

    @Test
    void generateTokenIncludesSubjectAndValidates() {
        JwtService jwtService = new JwtService(SECRET, EXPIRATION);

        String token = jwtService.generateToken("user@example.com");

        assertEquals("user@example.com", jwtService.extractUsername(token));
        assertTrue(jwtService.validateToken(token, "user@example.com"));
    }

    @Test
    void validateTokenFailsForDifferentUser() {
        JwtService jwtService = new JwtService(SECRET, EXPIRATION);
        String token = jwtService.generateToken("user@example.com");

        assertFalse(jwtService.validateToken(token, "another@example.com"));
    }

    @Test
    void extractExpirationReturnsFutureDate() {
        JwtService jwtService = new JwtService(SECRET, EXPIRATION);
        String token = jwtService.generateToken("user@example.com");

        Date expiration = jwtService.extractExpiration(token);

        assertTrue(expiration.after(new Date()));
    }
}
