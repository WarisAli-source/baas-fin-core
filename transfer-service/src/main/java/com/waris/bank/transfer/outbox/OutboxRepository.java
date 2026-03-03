package com.waris.bank.transfer.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<OutboxEvent, UUID> {

  @Query(value = "SELECT * FROM outbox_event WHERE status='NEW' ORDER BY created_at LIMIT 50", nativeQuery = true)
  List<OutboxEvent> fetchBatch();
}
