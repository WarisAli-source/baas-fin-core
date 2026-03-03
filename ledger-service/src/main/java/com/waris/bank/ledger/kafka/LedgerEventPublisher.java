package com.waris.bank.ledger.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.waris.bank.common.events.LedgerPostedEvent;
import com.waris.bank.common.events.Topics;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class LedgerEventPublisher {

  private final KafkaTemplate<String, String> kafka;
  private final ObjectMapper mapper = new ObjectMapper();

  public LedgerEventPublisher(KafkaTemplate<String, String> kafka) {
    this.kafka = kafka;
  }

  public void publish(LedgerPostedEvent evt) {
    try {
      kafka.send(Topics.LEDGER_EVENTS_V1, evt.txnId(), mapper.writeValueAsString(evt));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
