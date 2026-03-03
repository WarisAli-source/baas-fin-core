package com.waris.bank.auth.core;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    private final SecretKey key;
    private final long ttlSeconds;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.ttlSeconds}") long ttlSeconds) {

        if (secret == null || secret.trim().isEmpty()) {
            throw new IllegalArgumentException("app.jwt.secret must not be empty");
        }
        System.out.println("JWT Secret from config: " + secret);

        // Remove ALL whitespace (spaces/newlines/tabs) to make Base64 decoding stable
        String normalized = secret.replaceAll("\\s+", "");

        final byte[] keyBytes;
        try {
            keyBytes = Base64.getDecoder().decode(normalized);
        } catch (IllegalArgumentException e) {
            // Fail fast with clear message: secret is not valid Base64
            throw new IllegalArgumentException(
                    "Invalid app.jwt.secret: must be Base64 (no whitespace). " +
                            "Tip: use `openssl rand -base64 64 | tr -d '\\n'` and paste single-line.",
                    e
            );
        }

        // JJWT requires >= 32 bytes for HMAC SHA algorithms (HS256 minimum)
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException(
                    "JWT secret too short after Base64 decode: " + keyBytes.length +
                            " bytes. Need >= 32 bytes for HS256."
            );
        }

        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.ttlSeconds = ttlSeconds;
    }

    public long ttlSeconds() { return ttlSeconds; }

    public String issueToken(String userId, String role, String email) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(ttlSeconds);

        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .addClaims(Map.of(
                        "email", email,
                        "roles", role
                ))
                // Explicit algorithm to avoid mismatch
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}