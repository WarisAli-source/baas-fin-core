package com.waris.bank.transfer.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.waris.bank.common.events.LedgerPostedEvent;
import com.waris.bank.common.events.Topics;
import com.waris.bank.transfer.domain.Transfer;
import com.waris.bank.transfer.domain.TransferRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class LedgerEventsConsumer {

  private final ObjectMapper mapper = new ObjectMapper();
  private final TransferRepository transfers;

  public LedgerEventsConsumer(TransferRepository transfers) {
    this.transfers = transfers;
  }

  @KafkaListener(topics = Topics.LEDGER_EVENTS_V1)
  public void onLedgerEvent(ConsumerRecord<String, String> record) throws Exception {
    LedgerPostedEvent evt = mapper.readValue(record.value(), LedgerPostedEvent.class);

    if (evt.transferId() == null) return;

    UUID transferId = UUID.fromString(evt.transferId());
    transfers.findById(transferId).ifPresent(t -> {
      if ("POSTED".equals(evt.status())) {
        t.setStatus("COMPLETED");
      } else {
        t.setStatus("FAILED");
      }
      transfers.save(t);
    });
  }
}
