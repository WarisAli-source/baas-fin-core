package com.waris.bank.statement.api;

import com.waris.bank.statement.domain.StatementLine;
import com.waris.bank.statement.domain.StatementRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class StatementController {

  private final StatementRepository repo;

  public StatementController(StatementRepository repo) {
    this.repo = repo;
  }

  // MVP: user provides accountId for which they want statement
  @GetMapping("/api/v1/statements/{accountId}")
  public List<StatementLine> myStatement(@PathVariable UUID accountId, @AuthenticationPrincipal Jwt jwt) {
    // NOTE: Ownership check to be added when account-service lookup is integrated.
    return repo.findTop200ByAccountIdOrderByCreatedAtDesc(accountId);
  }

  // Admin search (simplified)
  @GetMapping("/api/v1/admin/accounts/{accountId}/transactions")
  public List<StatementLine> adminTransactions(@PathVariable UUID accountId) {
    return repo.findTop200ByAccountIdOrderByCreatedAtDesc(accountId);
  }
}
