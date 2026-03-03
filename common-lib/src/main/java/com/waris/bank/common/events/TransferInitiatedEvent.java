package com.waris.bank.common.events;

public record TransferInitiatedEvent(
    String eventId,
    String transferId,
    String txnId,
    String fromAccountId,
    String toAccountId,
    long amountPaise,
    String createdAt,
    String initiatedByUserId,
    String narrative
) {}
