package com.tidemart.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record EmailLoginRequest(@NotBlank String email, @NotBlank String password) {}
