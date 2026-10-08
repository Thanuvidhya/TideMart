package com.tidemart.notification;

import com.tidemart.common.ApiResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService service;

    public NotificationController(NotificationService service) { this.service = service; }

    @GetMapping
    public ApiResponse<NotificationService.View> view(@AuthenticationPrincipal Long uid) { return ApiResponse.ok(service.view(uid)); }

    @PostMapping("/read-all")
    public ApiResponse<Void> readAll(@AuthenticationPrincipal Long uid) { service.readAll(uid); return ApiResponse.ok(null); }
}
