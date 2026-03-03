package com.waris.bank.transfer.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "transfer")
public class Transfer {
  @Id
  @Column(name = "transfer_id")
  private UUID transferId;

  @Column(name = "txn_id", unique = true, nullable = false)
  private String txnId;

  @Column(name = "from_account_id", nullable = false)
  private UUID fromAccountId;

  @Column(name = "to_account_id", nullable = false)
  private UUID toAccountId;

  @Column(name = "amount_paise", nullable = false)
  private long amountPaise;

  @Column(nullable = false)
  private String status;

  @Column(name = "initiated_by", nullable = false)
  private UUID initiatedBy;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt = OffsetDateTime.now();

  public UUID getTransferId() { return transferId; }
  public void setTransferId(UUID transferId) { this.transferId = transferId; }
  public String getTxnId() { return txnId; }
  public void setTxnId(String txnId) { this.txnId = txnId; }
  public UUID getFromAccountId() { return fromAccountId; }
  public void setFromAccountId(UUID fromAccountId) { this.fromAccountId = fromAccountId; }
  public UUID getToAccountId() { return toAccountId; }
  public void setToAccountId(UUID toAccountId) { this.toAccountId = toAccountId; }
  public long getAmountPaise() { return amountPaise; }
  public void setAmountPaise(long amountPaise) { this.amountPaise = amountPaise; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public UUID getInitiatedBy() { return initiatedBy; }
  public void setInitiatedBy(UUID initiatedBy) { this.initiatedBy = initiatedBy; }
  public OffsetDateTime getCreatedAt() { return createdAt; }
}
