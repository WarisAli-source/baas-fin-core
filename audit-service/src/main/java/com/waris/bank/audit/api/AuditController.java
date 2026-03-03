package com.waris.bank.audit.api;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/audit")
public class AuditController {

  private final JdbcTemplate jdbc;

  public AuditController(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  @GetMapping("/recent")
  public List<Map<String, Object>> recent() {
    return jdbc.queryForList("SELECT * FROM audit_log ORDER BY created_at DESC LIMIT 50");
  }
}
