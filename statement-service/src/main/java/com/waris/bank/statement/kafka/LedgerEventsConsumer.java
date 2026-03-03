package com.waris.bank.statement.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.waris.bank.common.events.LedgerPostedEvent;
import com.waris.bank.common.events.Topics;
import com.waris.bank.statement.domain.StatementLine;
import com.waris.bank.statement.domain.StatementRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class LedgerEventsConsumer {

  private final ObjectMapper mapper = new ObjectMapper();
  private final StatementRepository repo;

  public LedgerEventsConsumer(StatementRepository repo) {
    this.repo = repo;
  }

  @KafkaListener(topics = Topics.LEDGER_EVENTS_V1)
  public void onEvent(ConsumerRecord<String, String> record) throws Exception {
    LedgerPostedEvent evt = mapper.readValue(record.value(), LedgerPostedEvent.class);
    // For MVP we only store transaction-level events. Later, enrich with debit/credit legs.
    if (evt.transferId() == null) return;

    // Minimal: record one generic line per account is done by ledger entries in future.
    // Here we log the transaction itself for admin tracking.
    StatementLine line = new StatementLine();
    line.setAccountId(UUID.fromString(evt.transferId())); // placeholder for MVP; will be improved in next iteration
    line.setTxnId(evt.txnId());
    line.setTransferId(UUID.fromString(evt.transferId()));
    line.setDirection(evt.status());
    line.setAmountPaise(0);
    line.setNarrative(evt.reason());
    repo.save(line);
  }
}
