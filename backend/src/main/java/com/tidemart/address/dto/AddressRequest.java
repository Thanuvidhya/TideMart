package com.tidemart.address.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AddressRequest(@NotBlank String name, @NotBlank String phone, @NotBlank String line1, @NotBlank String city, @NotBlank String state,
                             @Pattern(regexp = "\\d{6}", message = "must be 6 digits") String pincode, boolean isDefault) {}
