package com.tidemart.delivery;

import com.tidemart.common.ApiResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/delivery")
public class DeliveryController {
    private final DeliveryService service;
    private final com.tidemart.user.UserRepository users;
    public DeliveryController(DeliveryService service, com.tidemart.user.UserRepository users) { this.service = service; this.users = users; }

    @GetMapping("/orders")
    public ApiResponse<List<Map<String,Object>>> orders(@AuthenticationPrincipal Long uid) {
        var u = users.findById(uid).orElseThrow();
        if (u.phone == null) return ApiResponse.ok(List.of());
        return ApiResponse.ok(service.assigned(service.partnerIdForPhone(u.phone)));
    }

    @PostMapping("/orders/{id}/deliver")
    public ApiResponse<Void> deliver(@AuthenticationPrincipal Long uid, @PathVariable Long id, @RequestParam String otp) {
        var u = users.findById(uid).orElseThrow(); service.deliver(id, otp, service.partnerIdForPhone(u.phone)); return ApiResponse.ok(null);
    }

    @PostMapping("/orders/{id}/failed")
    public ApiResponse<Map<String,String>> failed(@AuthenticationPrincipal Long uid, @PathVariable Long id, @RequestParam(required=false) String note) {
        var u = users.findById(uid).orElseThrow(); return ApiResponse.ok(Map.of("result", service.failed(id, note, service.partnerIdForPhone(u.phone))));
    }
}
