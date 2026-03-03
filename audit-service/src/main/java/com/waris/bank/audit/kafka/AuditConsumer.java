package com.waris.bank.audit.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.waris.bank.audit.store.AuditRepo;
import com.waris.bank.common.events.AuditEvent;
import com.waris.bank.common.events.Topics;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AuditConsumer {

  private final AuditRepo repo;
  private final ObjectMapper mapper = new ObjectMapper();

  public AuditConsumer(AuditRepo repo) {
    this.repo = repo;
  }

  @KafkaListener(topics = Topics.AUDIT_EVENTS_V1)
  public void onAudit(ConsumerRecord<String, String> record) throws Exception {
    AuditEvent evt = mapper.readValue(record.value(), AuditEvent.class);
    repo.insert(evt.eventId(), evt.actorUserId(), evt.actorRole(), evt.action(), evt.targetType(), evt.targetId(), evt.payload());
  }
}
