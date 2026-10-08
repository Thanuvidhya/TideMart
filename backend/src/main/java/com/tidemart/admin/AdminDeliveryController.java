package com.tidemart.admin;

import com.tidemart.common.ApiResponse;
import com.tidemart.delivery.DeliveryService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/delivery")
public class AdminDeliveryController {
    private final DeliveryService service;

    public AdminDeliveryController(DeliveryService service) { this.service = service; }

    /** The OTP is returned here only so you can test the demo. The customer also gets it as an alert. */
    @PostMapping("/{orderId}/assign")
    public ApiResponse<Map<String, String>> assign(@PathVariable Long orderId, @RequestParam Long partnerId) { return ApiResponse.ok(Map.of("demoOtp", service.assign(orderId, partnerId))); }

    @PostMapping("/{orderId}/deliver")
    public ApiResponse<Void> deliver(@PathVariable Long orderId, @RequestParam String otp) { service.deliver(orderId, otp); return ApiResponse.ok(null); }

    @PostMapping("/{orderId}/failed")
    public ApiResponse<Map<String, String>> failed(@PathVariable Long orderId, @RequestParam(required = false) String note) { return ApiResponse.ok(Map.of("result", service.failed(orderId, note))); }
}
