package com.waris.bank.auth.api;

import com.waris.bank.auth.api.dto.AuthResponse;
import com.waris.bank.auth.api.dto.LoginRequest;
import com.waris.bank.auth.api.dto.SignupRequest;
import com.waris.bank.auth.core.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final AuthService service;

  public AuthController(AuthService service) {
    this.service = service;
  }

  @PostMapping("/signup")
  @ResponseStatus(HttpStatus.CREATED)
  public AuthResponse signup(@Valid @RequestBody SignupRequest req) {
    return service.signup(req);
  }

  @PostMapping("/login")
  public AuthResponse login(@Valid @RequestBody LoginRequest req) {
    return service.login(req);
  }

  // DEV ONLY
  @PostMapping("/admin")
  @ResponseStatus(HttpStatus.CREATED)
  public AuthResponse createAdmin(@Valid @RequestBody SignupRequest req) {
    return service.createAdmin(req);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public String bad(IllegalArgumentException ex) {
    return ex.getMessage();
  }
}
