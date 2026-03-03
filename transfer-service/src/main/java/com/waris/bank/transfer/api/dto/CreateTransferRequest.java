package com.waris.bank.transfer.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record CreateTransferRequest(
    @NotBlank String fromAccountId,
    @NotBlank String toAccountId,
    @Positive long amountPaise,
    String narrative
) {}
