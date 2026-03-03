package com.waris.bank.statement.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "statement_line")
public class StatementLine {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "account_id", nullable = false)
  private UUID accountId;

  @Column(name = "txn_id", nullable = false)
  private String txnId;

  @Column(name = "transfer_id")
  private UUID transferId;

  @Column(nullable = false)
  private String direction;

  @Column(name = "amount_paise", nullable = false)
  private long amountPaise;

  private String narrative;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt = OffsetDateTime.now();

  public Long getId() { return id; }
  public UUID getAccountId() { return accountId; }
  public void setAccountId(UUID accountId) { this.accountId = accountId; }
  public String getTxnId() { return txnId; }
  public void setTxnId(String txnId) { this.txnId = txnId; }
  public UUID getTransferId() { return transferId; }
  public void setTransferId(UUID transferId) { this.transferId = transferId; }
  public String getDirection() { return direction; }
  public void setDirection(String direction) { this.direction = direction; }
  public long getAmountPaise() { return amountPaise; }
  public void setAmountPaise(long amountPaise) { this.amountPaise = amountPaise; }
  public String getNarrative() { return narrative; }
  public void setNarrative(String narrative) { this.narrative = narrative; }
  public OffsetDateTime getCreatedAt() { return createdAt; }
}
