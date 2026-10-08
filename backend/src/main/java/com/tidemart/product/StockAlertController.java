package com.tidemart.product;

import com.tidemart.common.ApiResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class StockAlertController {
    private final StockAlertRepository repo;

    public StockAlertController(StockAlertRepository repo) { this.repo = repo; }

    @PostMapping("/{id}/alert")
    public ApiResponse<Void> subscribe(@AuthenticationPrincipal Long uid, @PathVariable Long id) {
        if (repo.findByUserIdAndProductId(uid, id).isEmpty()) { StockAlert a = new StockAlert(); a.userId = uid; a.productId = id; repo.save(a); }
        return ApiResponse.ok(null);
    }
}
