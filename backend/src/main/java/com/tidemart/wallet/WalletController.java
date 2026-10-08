package com.tidemart.wallet;

import com.tidemart.common.ApiResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {
    private final WalletService service;

    public WalletController(WalletService service) { this.service = service; }

    @GetMapping
    public ApiResponse<WalletService.View> view(@AuthenticationPrincipal Long uid) { return ApiResponse.ok(service.view(uid)); }
}
