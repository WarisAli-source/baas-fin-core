package com.waris.bank.analytics.kafka;

import com.waris.bank.analytics.store.MetricsRepository;
import com.waris.bank.common.events.Topics;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AnalyticsConsumers {

  private final MetricsRepository metrics;

  public AnalyticsConsumers(MetricsRepository metrics) {
    this.metrics = metrics;
  }

  @KafkaListener(topics = Topics.TRANSFER_COMMANDS_V1)
  public void onTransferCmd(ConsumerRecord<String, String> record) {
    metrics.inc("transfer.commands.count");
  }

  @KafkaListener(topics = Topics.LEDGER_EVENTS_V1)
  public void onLedgerEvt(ConsumerRecord<String, String> record) {
    metrics.inc("ledger.events.count");
  }
}
