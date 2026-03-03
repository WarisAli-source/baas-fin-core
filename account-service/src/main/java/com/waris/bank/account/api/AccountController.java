package com.waris.bank.account.api;

import com.waris.bank.account.api.dto.CreateAccountResponse;
import com.waris.bank.account.domain.BankAccount;
import com.waris.bank.account.domain.BankAccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

  private final BankAccountRepository repo;

  public AccountController(BankAccountRepository repo) {
    this.repo = repo;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public CreateAccountResponse create(@AuthenticationPrincipal Jwt jwt) {
    UUID userId = UUID.fromString(jwt.getSubject());
    BankAccount a = new BankAccount();
    a.setAccountId(UUID.randomUUID());
    a.setUserId(userId);
    a.setStatus("ACTIVE");
    repo.save(a);
    return new CreateAccountResponse(a.getAccountId().toString(), a.getStatus());
  }

  @GetMapping
  public List<BankAccount> myAccounts(@AuthenticationPrincipal Jwt jwt) {
    UUID userId = UUID.fromString(jwt.getSubject());
    return repo.findByUserId(userId);
  }

  @PostMapping("/{accountId}/freeze")
  @PreAuthorize("hasAuthority('SCOPE_ADMIN') or hasRole('ADMIN')")
  public void freeze(@PathVariable UUID accountId) {
    BankAccount a = repo.findById(accountId).orElseThrow();
    a.setStatus("FROZEN");
    repo.save(a);
  }
}
