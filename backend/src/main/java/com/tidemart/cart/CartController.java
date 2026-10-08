package com.tidemart.cart;

import com.tidemart.common.ApiResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    public record AddRequest(Long productId, Long variantId, int qty) {}
    private final CartService service;

    public CartController(CartService service) { this.service = service; }

    @GetMapping
    public ApiResponse<CartService.View> view(@AuthenticationPrincipal Long uid, @RequestParam(required = false) String coupon, @RequestParam(required = false) String pay) { return ApiResponse.ok(service.view(uid, coupon, pay)); }

    @PostMapping
    public ApiResponse<CartService.View> add(@AuthenticationPrincipal Long uid, @RequestBody AddRequest r) { service.add(uid, r.productId(), r.variantId(), r.qty()); return ApiResponse.ok(service.view(uid, null, null)); }

    @PutMapping("/{itemId}/save")
    public ApiResponse<CartService.View> save(@AuthenticationPrincipal Long uid, @PathVariable Long itemId, @RequestParam boolean saved) { service.setSaved(uid, itemId, saved); return ApiResponse.ok(service.view(uid, null, null)); }

    @PutMapping("/{itemId}")
    public ApiResponse<CartService.View> qty(@AuthenticationPrincipal Long uid, @PathVariable Long itemId, @RequestParam int qty, @RequestParam(required = false) String coupon) { service.setQty(uid, itemId, qty); return ApiResponse.ok(service.view(uid, coupon, null)); }
}
