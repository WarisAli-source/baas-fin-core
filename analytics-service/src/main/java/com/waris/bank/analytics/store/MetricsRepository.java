package com.waris.bank.analytics.store;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class MetricsRepository {

  private final JdbcTemplate jdbc;

  public MetricsRepository(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  public void inc(String metric) {
    jdbc.update("INSERT INTO metrics_counter(metric,value) VALUES (?,1) ON CONFLICT (metric) DO UPDATE SET value = metrics_counter.value + 1, updated_at=now()", metric);
  }

  public long get(String metric) {
    Long v = jdbc.queryForObject("SELECT value FROM metrics_counter WHERE metric=?", Long.class, metric);
    return v == null ? 0 : v;
  }
}
