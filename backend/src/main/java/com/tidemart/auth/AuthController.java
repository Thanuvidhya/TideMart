package com.tidemart.auth;

import com.tidemart.auth.dto.*;
import com.tidemart.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService service;

    public AuthController(AuthService service) { this.service = service; }

    @PostMapping("/send-otp")
    public ApiResponse<Map<String, String>> sendOtp(@Valid @RequestBody SendOtpRequest r) {
        String code = service.sendOtp(r.phone());
        return ApiResponse.ok(code == null ? Map.of() : Map.of("demoOtp", code));
    }

    @PostMapping("/verify-otp")
    public ApiResponse<AuthResponse> verify(@Valid @RequestBody VerifyOtpRequest r) { return ApiResponse.ok(service.verifyOtp(r.phone(), r.code())); }

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest r) { return ApiResponse.ok(service.register(r)); }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody EmailLoginRequest r) { return ApiResponse.ok(service.login(r)); }

    @PostMapping("/customer-login")
    public ApiResponse<AuthResponse> customerLogin(@Valid @RequestBody EmailLoginRequest r) { return ApiResponse.ok(service.login(r)); }

    @PostMapping("/admin-login")
    public ApiResponse<AuthResponse> adminLogin(@Valid @RequestBody EmailLoginRequest r) { return ApiResponse.ok(service.adminLogin(r)); }

    @PostMapping("/delivery-login")
    public ApiResponse<AuthResponse> deliveryLogin(@Valid @RequestBody EmailLoginRequest r) { return ApiResponse.ok(service.deliveryLogin(r)); }
}
