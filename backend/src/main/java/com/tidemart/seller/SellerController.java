package com.tidemart.seller;

import com.tidemart.common.ApiResponse;
import com.tidemart.order.Order;
import com.tidemart.product.Product;
import com.tidemart.product.ProductVariant;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SellerController {
    private final SellerService s;

    public SellerController(SellerService s) { this.s = s; }

    @PostMapping("/sellers/signup")
    public ApiResponse<Seller> signup(@AuthenticationPrincipal Long uid, @RequestBody SellerService.SignupRequest r) { return ApiResponse.ok(s.signup(uid, r)); }

    @GetMapping("/seller/me")
    public ApiResponse<Map<String, Object>> me(@AuthenticationPrincipal Long uid) { return ApiResponse.ok(s.me(uid)); }

    @PutMapping("/seller/pickup")
    public ApiResponse<PickupAddress> pickup(@AuthenticationPrincipal Long uid, @RequestBody SellerService.PickupRequest r) { return ApiResponse.ok(s.savePickup(uid, r)); }

    @GetMapping("/seller/products")
    public ApiResponse<List<SellerService.ProductRow>> products(@AuthenticationPrincipal Long uid) { return ApiResponse.ok(s.listProducts(uid)); }

    @PostMapping("/seller/products")
    public ApiResponse<SellerService.ProductRow> create(@AuthenticationPrincipal Long uid, @RequestBody SellerService.ProductRequest r) { return ApiResponse.ok(s.createProduct(uid, r)); }

    @PutMapping("/seller/products/{id}")
    public ApiResponse<Product> price(@AuthenticationPrincipal Long uid, @PathVariable Long id, @RequestParam double price, @RequestParam double mrp) { return ApiResponse.ok(s.updatePrice(uid, id, price, mrp)); }

    @PutMapping("/seller/variants/{id}/stock")
    public ApiResponse<ProductVariant> stock(@AuthenticationPrincipal Long uid, @PathVariable Long id, @RequestParam int stock) { return ApiResponse.ok(s.setStock(uid, id, stock)); }

    @GetMapping("/seller/orders")
    public ApiResponse<List<SellerService.OrderLine>> orders(@AuthenticationPrincipal Long uid) { return ApiResponse.ok(s.orders(uid)); }

    @PostMapping("/seller/orders/{id}/status")
    public ApiResponse<Order> status(@AuthenticationPrincipal Long uid, @PathVariable Long id, @RequestParam String to) { return ApiResponse.ok(s.setStatus(uid, id, to)); }

    @GetMapping("/seller/orders/{id}/label")
    public ApiResponse<SellerService.Label> label(@AuthenticationPrincipal Long uid, @PathVariable Long id) { return ApiResponse.ok(s.label(uid, id)); }

    @GetMapping("/seller/analytics")
    public ApiResponse<List<SellerService.Stat>> analytics(@AuthenticationPrincipal Long uid) { return ApiResponse.ok(s.analytics(uid)); }

    @PostMapping("/seller/products/bulk")
    public ApiResponse<Map<String, Object>> bulk(@AuthenticationPrincipal Long uid, @RequestBody String csv) { return ApiResponse.ok(s.bulk(uid, csv)); }

    @PostMapping("/seller/orders/{id}/reject")
    public ApiResponse<Order> reject(@AuthenticationPrincipal Long uid, @PathVariable Long id, @RequestParam String reason) { return ApiResponse.ok(s.reject(uid, id, reason)); }

    @GetMapping("/seller/earnings")
    public ApiResponse<SellerService.Earnings> earnings(@AuthenticationPrincipal Long uid) { return ApiResponse.ok(s.earnings(uid)); }
}
