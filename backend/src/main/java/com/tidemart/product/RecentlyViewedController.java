package com.tidemart.product;

import com.tidemart.common.ApiResponse;
import com.tidemart.product.dto.ProductCard;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/recently-viewed")
public class RecentlyViewedController {
    private final RecentlyViewedRepository repo;
    private final ProductRepository products;
    private final ProductService service;

    public RecentlyViewedController(RecentlyViewedRepository repo, ProductRepository products, ProductService service) { this.repo = repo; this.products = products; this.service = service; }

    @PostMapping("/{productId}")
    public ApiResponse<Void> mark(@AuthenticationPrincipal Long uid, @PathVariable Long productId) {
        RecentlyViewed r = repo.findByUserIdAndProductId(uid, productId).orElseGet(RecentlyViewed::new);
        r.userId = uid; r.productId = productId; r.viewedAt = LocalDateTime.now();
        repo.save(r);
        return ApiResponse.ok(null);
    }

    @GetMapping
    public ApiResponse<List<ProductCard>> list(@AuthenticationPrincipal Long uid) {
        return ApiResponse.ok(repo.findTop8ByUserIdOrderByViewedAtDesc(uid).stream().map(r -> products.findById(r.productId).orElse(null)).filter(p -> p != null).map(service::card).toList());
    }
}
