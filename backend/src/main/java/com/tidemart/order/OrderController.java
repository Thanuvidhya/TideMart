package com.tidemart.order;

import com.tidemart.common.ApiResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService service;

    public OrderController(OrderService service) { this.service = service; }

    @PostMapping
    public ApiResponse<Order> place(@AuthenticationPrincipal Long uid, @RequestBody OrderService.PlaceRequest r) { return ApiResponse.ok(service.place(uid, r)); }

    @GetMapping
    public ApiResponse<List<OrderService.Summary>> list(@AuthenticationPrincipal Long uid) { return ApiResponse.ok(service.list(uid)); }

    @GetMapping("/{id}")
    public ApiResponse<OrderService.Detail> detail(@AuthenticationPrincipal Long uid, @PathVariable Long id) { return ApiResponse.ok(service.detail(uid, id)); }

    @PostMapping("/{id}/cancel")
    public ApiResponse<OrderService.Detail> cancel(@AuthenticationPrincipal Long uid, @PathVariable Long id) { return ApiResponse.ok(service.cancel(uid, id)); }

    @PostMapping("/{id}/advance")
    public ApiResponse<OrderService.Detail> advance(@AuthenticationPrincipal Long uid, @PathVariable Long id) { return ApiResponse.ok(service.advance(uid, id)); }
}
