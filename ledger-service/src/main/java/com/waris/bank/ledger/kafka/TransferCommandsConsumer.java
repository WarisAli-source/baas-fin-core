package com.waris.bank.ledger.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.waris.bank.common.events.LedgerPostedEvent;
import com.waris.bank.common.events.TransferInitiatedEvent;
import com.waris.bank.common.events.Topics;
import com.waris.bank.ledger.store.LedgerRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
public class TransferCommandsConsumer {

  private final ObjectMapper mapper = new ObjectMapper();
  private final LedgerRepository repo;
  private final LedgerEventPublisher publisher;

  public TransferCommandsConsumer(LedgerRepository repo, LedgerEventPublisher publisher) {
    this.repo = repo;
    this.publisher = publisher;
  }

  @KafkaListener(topics = Topics.TRANSFER_COMMANDS_V1)
  public void onMessage(ConsumerRecord<String, String> record) throws Exception {
    TransferInitiatedEvent cmd = mapper.readValue(record.value(), TransferInitiatedEvent.class);

    String txnId = cmd.txnId();
    if (repo.txnExists(txnId)) {
      publisher.publish(new LedgerPostedEvent(UUID.randomUUID().toString(), txnId, cmd.transferId(), "POSTED", OffsetDateTime.now().toString(), "DUPLICATE"));
      return;
    }

    UUID from = UUID.fromString(cmd.fromAccountId());
    UUID to = UUID.fromString(cmd.toAccountId());
    long amt = cmd.amountPaise();

    long available = repo.getAvailable(from);
    if (available < amt) {
      publisher.publish(new LedgerPostedEvent(UUID.randomUUID().toString(), txnId, cmd.transferId(), "FAILED", OffsetDateTime.now().toString(), "INSUFFICIENT_FUNDS"));
      return;
    }

    repo.createTxn(txnId, cmd.transferId(), "TRANSFER", "POSTING");
    repo.postEntry(txnId, from, "DEBIT", amt, cmd.narrative());
    repo.postEntry(txnId, to, "CREDIT", amt, cmd.narrative());
    repo.markTxn(txnId, "POSTED");

    publisher.publish(new LedgerPostedEvent(UUID.randomUUID().toString(), txnId, cmd.transferId(), "POSTED", OffsetDateTime.now().toString(), null));
  }
}
