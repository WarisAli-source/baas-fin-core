package com.waris.bank.transfer.api;

import com.waris.bank.transfer.api.dto.CreateTransferRequest;
import com.waris.bank.transfer.api.dto.TransferResponse;
import com.waris.bank.transfer.core.TransferOrchestration;
import com.waris.bank.transfer.domain.Transfer;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transfers")
public class TransferController {

  private final TransferOrchestration orchestration;

  public TransferController(TransferOrchestration orchestration) {
    this.orchestration = orchestration;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public TransferResponse create(@RequestHeader("Idempotency-Key") String key,
                                 @AuthenticationPrincipal Jwt jwt,
                                 @Valid @RequestBody CreateTransferRequest req) throws Exception {
    Transfer t = orchestration.create(req, jwt.getSubject(), key);
    return new TransferResponse(t.getTransferId().toString(), t.getTxnId(), t.getStatus());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public String bad(IllegalArgumentException ex) {
    return ex.getMessage();
  }
}
