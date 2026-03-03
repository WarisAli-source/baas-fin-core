package com.waris.bank.common.events;

public record LedgerPostedEvent(
    String eventId,
    String txnId,
    String transferId,
    String status, // POSTED or FAILED
    String postedAt,
    String reason
) {}
