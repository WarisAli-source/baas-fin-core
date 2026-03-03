package com.waris.bank.analytics.api;

import com.waris.bank.analytics.store.MetricsRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/metrics")
public class MetricsController {

  private final MetricsRepository repo;

  public MetricsController(MetricsRepository repo) {
    this.repo = repo;
  }

  @GetMapping("/overview")
  public Map<String, Long> overview() {
    return Map.of(
        "transfer.commands.count", repo.get("transfer.commands.count"),
        "ledger.events.count", repo.get("ledger.events.count")
    );
  }
}
