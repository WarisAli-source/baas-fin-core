package com.waris.bank.transfer.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.waris.bank.common.events.Topics;
import com.waris.bank.common.events.TransferInitiatedEvent;
import com.waris.bank.transfer.api.dto.CreateTransferRequest;
import com.waris.bank.transfer.domain.Transfer;
import com.waris.bank.transfer.domain.TransferRepository;
import com.waris.bank.transfer.grpc.LedgerClient;
import com.waris.bank.transfer.outbox.OutboxEvent;
import com.waris.bank.transfer.outbox.OutboxRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class TransferOrchestration {

  private final TransferRepository transfers;
  private final OutboxRepository outbox;
  private final LedgerClient ledger;
  private final ObjectMapper mapper = new ObjectMapper();

  public TransferOrchestration(TransferRepository transfers, OutboxRepository outbox, LedgerClient ledger) {
    this.transfers = transfers;
    this.outbox = outbox;
    this.ledger = ledger;
  }

  @Transactional
  public Transfer create(CreateTransferRequest req, String userId, String idempotencyKey) throws Exception {
    // Idempotency: if transfer exists with txnId=idempotencyKey -> return it
    return transfers.findByTxnId(idempotencyKey).orElseGet(() -> {
      try {
        long available = ledger.getAvailablePaise(req.fromAccountId());
        if (available < req.amountPaise()) {
          throw new IllegalArgumentException("INSUFFICIENT_FUNDS");
        }

        Transfer t = new Transfer();
        t.setTransferId(UUID.randomUUID());
        t.setTxnId(idempotencyKey);
        t.setFromAccountId(UUID.fromString(req.fromAccountId()));
        t.setToAccountId(UUID.fromString(req.toAccountId()));
        t.setAmountPaise(req.amountPaise());
        t.setStatus("INITIATED");
        t.setInitiatedBy(UUID.fromString(userId));
        transfers.save(t);

        TransferInitiatedEvent evt = new TransferInitiatedEvent(
            UUID.randomUUID().toString(),
            t.getTransferId().toString(),
            t.getTxnId(),
            req.fromAccountId(),
            req.toAccountId(),
            req.amountPaise(),
            OffsetDateTime.now().toString(),
            userId,
            req.narrative() == null ? "P2P_TRANSFER" : req.narrative()
        );

        OutboxEvent oe = new OutboxEvent();
        oe.setEventId(UUID.randomUUID());
        oe.setAggregateId(t.getTransferId());
        oe.setEventType(Topics.TRANSFER_COMMANDS_V1);
        oe.setPayload(mapper.writeValueAsString(evt));
        oe.setStatus("NEW");
        outbox.save(oe);

        return t;
      } catch (RuntimeException e) {
        throw e;
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });
  }
}
