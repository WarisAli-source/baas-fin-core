package com.waris.bank.common.events;

public record AuditEvent(
    String eventId,
    String actorUserId,
    String actorRole,
    String action,
    String targetType,
    String targetId,
    String payload,
    String createdAt
) {}
