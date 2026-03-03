package com.waris.bank.auth.api.dto;

public record AuthResponse(
    String accessToken,
    long expiresInSeconds,
    String userId,
    String role
) {}
