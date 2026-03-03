package com.waris.bank.transfer.outbox;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "outbox_event")
public class OutboxEvent {
  @Id
  @Column(name = "event_id")
  private UUID eventId;

  @Column(name = "aggregate_id", nullable = false)
  private UUID aggregateId;

  @Column(name = "event_type", nullable = false)
  private String eventType;

  @Column(columnDefinition = "jsonb", nullable = false)
  private String payload;

  @Column(nullable = false)
  private String status;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt = OffsetDateTime.now();

  @Column(name = "sent_at")
  private OffsetDateTime sentAt;

  public UUID getEventId() { return eventId; }
  public void setEventId(UUID eventId) { this.eventId = eventId; }
  public UUID getAggregateId() { return aggregateId; }
  public void setAggregateId(UUID aggregateId) { this.aggregateId = aggregateId; }
  public String getEventType() { return eventType; }
  public void setEventType(String eventType) { this.eventType = eventType; }
  public String getPayload() { return payload; }
  public void setPayload(String payload) { this.payload = payload; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public OffsetDateTime getCreatedAt() { return createdAt; }
  public OffsetDateTime getSentAt() { return sentAt; }
  public void setSentAt(OffsetDateTime sentAt) { this.sentAt = sentAt; }
}
