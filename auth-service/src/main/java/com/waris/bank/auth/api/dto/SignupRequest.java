package com.waris.bank.auth.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignupRequest(
    @Email @NotBlank String email,
    @NotBlank String password
) {}
