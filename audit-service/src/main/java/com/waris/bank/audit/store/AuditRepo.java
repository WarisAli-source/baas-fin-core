package com.waris.bank.audit.store;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AuditRepo {

  private final JdbcTemplate jdbc;

  public AuditRepo(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  public void insert(String eventId, String actorUserId, String actorRole, String action,
                     String targetType, String targetId, String payload) {
    jdbc.update("INSERT INTO audit_log(event_id, actor_user_id, actor_role, action, target_type, target_id, payload) VALUES (?,?,?,?,?,?,?)",
        eventId, actorUserId, actorRole, action, targetType, targetId, payload);
  }
}
