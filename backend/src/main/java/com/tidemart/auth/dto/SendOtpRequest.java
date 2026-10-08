package com.tidemart.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SendOtpRequest(@NotBlank @Pattern(regexp = "\\d{10}", message = "must be 10 digits") String phone) {}
