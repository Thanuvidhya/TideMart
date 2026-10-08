package com.tidemart.auth.dto;
public record AuthResponse(String token, Long userId, String role, String name, java.util.List<String> roles) {}
