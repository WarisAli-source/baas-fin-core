package com.waris.bank.transfer.api.dto;

public record TransferResponse(
    String transferId,
    String txnId,
    String status
) {}
