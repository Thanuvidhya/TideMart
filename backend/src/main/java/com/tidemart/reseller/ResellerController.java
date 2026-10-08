package com.tidemart.reseller;

import com.tidemart.common.ApiResponse;
import com.tidemart.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ResellerController {
    private final ResellerService s;

    public ResellerController(ResellerService s) { this.s = s; }

    @PostMapping("/resellers/signup")
    public ApiResponse<Reseller> signup(@AuthenticationPrincipal Long uid, @RequestBody ResellerService.SignupRequest r) { return ApiResponse.ok(s.signup(uid, r)); }

    @GetMapping("/reseller/me")
    public ApiResponse<Reseller> me(@AuthenticationPrincipal Long uid) { return ApiResponse.ok(s.me(uid)); }

    @PutMapping("/reseller/payout")
    public ApiResponse<Reseller> payout(@AuthenticationPrincipal Long uid, @RequestBody ResellerService.SignupRequest r) { return ApiResponse.ok(s.savePayout(uid, r)); }

    @GetMapping("/reseller/catalog")
    public ApiResponse<Page<ResellerService.CatalogItem>> catalog(@AuthenticationPrincipal Long uid, @RequestParam(defaultValue = "") String q, @RequestParam(defaultValue = "0") long category, @RequestParam(defaultValue = "0") int page) { return ApiResponse.ok(s.catalog(uid, q, category, page)); }

    @PutMapping("/reseller/margin")
    public ApiResponse<ResellerService.CatalogItem> margin(@AuthenticationPrincipal Long uid, @RequestParam Long productId, @RequestParam double margin) { return ApiResponse.ok(s.setMargin(uid, productId, margin)); }

    @GetMapping("/reseller/share")
    public ApiResponse<Map<String, String>> share(@AuthenticationPrincipal Long uid, @RequestParam List<Long> ids) { return ApiResponse.ok(s.share(uid, ids)); }

    @PostMapping("/reseller/orders")
    public ApiResponse<Order> place(@AuthenticationPrincipal Long uid, @RequestBody ResellerService.CustomerOrder r) { return ApiResponse.ok(s.placeOrder(uid, r)); }

    @GetMapping("/reseller/orders")
    public ApiResponse<List<ResellerService.OrderRow>> orders(@AuthenticationPrincipal Long uid) { return ApiResponse.ok(s.orders(uid)); }

    @GetMapping("/reseller/customers")
    public ApiResponse<List<ResellerCustomer>> customers(@AuthenticationPrincipal Long uid) { return ApiResponse.ok(s.customers(uid)); }

    @GetMapping("/reseller/earnings")
    public ApiResponse<ResellerService.Earnings> earnings(@AuthenticationPrincipal Long uid) { return ApiResponse.ok(s.earnings(uid)); }
}
