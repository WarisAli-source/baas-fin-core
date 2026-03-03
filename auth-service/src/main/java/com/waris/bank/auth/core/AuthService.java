package com.waris.bank.auth.core;

import com.waris.bank.auth.api.dto.AuthResponse;
import com.waris.bank.auth.api.dto.LoginRequest;
import com.waris.bank.auth.api.dto.SignupRequest;
import com.waris.bank.auth.domain.User;
import com.waris.bank.auth.domain.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {

  private final UserRepository repo;
  private final PasswordEncoder encoder;
  private final JwtService jwt;

  public AuthService(UserRepository repo, PasswordEncoder encoder, JwtService jwt) {
    this.repo = repo;
    this.encoder = encoder;
    this.jwt = jwt;
  }

  public AuthResponse signup(SignupRequest req) {
    repo.findByEmail(req.email()).ifPresent(u -> { throw new IllegalArgumentException("EMAIL_ALREADY_EXISTS"); });
    User u = new User();
    u.setUserId(UUID.randomUUID());
    u.setEmail(req.email().toLowerCase());
    u.setPasswordHash(encoder.encode(req.password()));
    u.setRole("USER");
    repo.save(u);

    String token = jwt.issueToken(u.getUserId().toString(), u.getRole(), u.getEmail());
    return new AuthResponse(token, jwt.ttlSeconds(), u.getUserId().toString(), u.getRole());
  }

  public AuthResponse login(LoginRequest req) {
    User u = repo.findByEmail(req.email().toLowerCase()).orElseThrow(() -> new IllegalArgumentException("INVALID_CREDENTIALS"));
    if (!encoder.matches(req.password(), u.getPasswordHash())) {
      throw new IllegalArgumentException("INVALID_CREDENTIALS");
    }
    String token = jwt.issueToken(u.getUserId().toString(), u.getRole(), u.getEmail());
    return new AuthResponse(token, jwt.ttlSeconds(), u.getUserId().toString(), u.getRole());
  }

  // Simple dev helper endpoint to create an admin if needed
  public AuthResponse createAdmin(SignupRequest req) {
    repo.findByEmail(req.email()).ifPresent(x -> { throw new IllegalArgumentException("EMAIL_ALREADY_EXISTS"); });
    User u = new User();
    u.setUserId(UUID.randomUUID());
    u.setEmail(req.email().toLowerCase());
    u.setPasswordHash(encoder.encode(req.password()));
    u.setRole("ADMIN");
    repo.save(u);
    String token = jwt.issueToken(u.getUserId().toString(), u.getRole(), u.getEmail());
    return new AuthResponse(token, jwt.ttlSeconds(), u.getUserId().toString(), u.getRole());
  }
}
