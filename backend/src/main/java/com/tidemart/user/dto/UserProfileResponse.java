package com.tidemart.user.dto;

public record UserProfileResponse(Long id, String name, String phone, String email, String photoUrl, String language, String role, String referralCode) {}
