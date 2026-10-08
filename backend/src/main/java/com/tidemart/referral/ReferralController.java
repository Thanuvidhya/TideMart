package com.tidemart.referral;

import com.tidemart.common.ApiResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/referrals")
public class ReferralController {
    private final ReferralService service;

    public ReferralController(ReferralService service) { this.service = service; }

    @GetMapping
    public ApiResponse<ReferralService.View> view(@AuthenticationPrincipal Long uid) { return ApiResponse.ok(service.view(uid)); }

    @PostMapping("/apply")
    public ApiResponse<ReferralService.View> apply(@AuthenticationPrincipal Long uid, @RequestParam String code) { return ApiResponse.ok(service.apply(uid, code)); }
}
