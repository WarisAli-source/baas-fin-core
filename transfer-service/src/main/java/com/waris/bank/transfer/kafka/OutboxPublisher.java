package com.waris.bank.transfer.kafka;

import com.waris.bank.common.events.Topics;
import com.waris.bank.transfer.outbox.OutboxEvent;
import com.waris.bank.transfer.outbox.OutboxRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
@EnableScheduling
public class OutboxPublisher {

  private final OutboxRepository repo;
  private final KafkaTemplate<String, String> kafka;

  public OutboxPublisher(OutboxRepository repo, KafkaTemplate<String, String> kafka) {
    this.repo = repo;
    this.kafka = kafka;
  }

  @Scheduled(fixedDelayString = "${app.outbox.publishDelayMs:2000}")
  public void publish() {
    for (OutboxEvent e : repo.fetchBatch()) {
      kafka.send(Topics.TRANSFER_COMMANDS_V1, e.getAggregateId().toString(), e.getPayload());
      e.setStatus("SENT");
      e.setSentAt(OffsetDateTime.now());
      repo.save(e);
    }
  }
}
