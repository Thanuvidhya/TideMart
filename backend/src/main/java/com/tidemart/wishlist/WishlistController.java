package com.tidemart.wishlist;

import com.tidemart.common.ApiResponse;
import com.tidemart.product.ProductRepository;
import com.tidemart.product.ProductService;
import com.tidemart.product.dto.ProductCard;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {
    private final WishlistRepository repo;
    private final ProductRepository products;
    private final ProductService productService;

    public WishlistController(WishlistRepository repo, ProductRepository products, ProductService productService) { this.repo = repo; this.products = products; this.productService = productService; }

    @GetMapping
    public ApiResponse<List<ProductCard>> list(@AuthenticationPrincipal Long uid) { return ApiResponse.ok(cards(uid)); }

    /** Adds the product if it is not saved yet, removes it if it is. */
    @PostMapping("/{productId}")
    public ApiResponse<List<ProductCard>> toggle(@AuthenticationPrincipal Long uid, @PathVariable Long productId) {
        var existing = repo.findByUserIdAndProductId(uid, productId);
        if (existing.isPresent()) repo.delete(existing.get());
        else { WishlistItem w = new WishlistItem(); w.userId = uid; w.productId = productId; repo.save(w); }
        return ApiResponse.ok(cards(uid));
    }

    private List<ProductCard> cards(Long uid) {
        return repo.findByUserId(uid).stream().map(w -> products.findById(w.productId).orElse(null)).filter(p -> p != null).map(productService::card).toList();
    }
}
