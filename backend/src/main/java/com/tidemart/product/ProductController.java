package com.tidemart.product;

import com.tidemart.common.ApiResponse;
import com.tidemart.product.dto.ProductCard;
import com.tidemart.product.dto.ProductDetail;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService service;

    public ProductController(ProductService service) { this.service = service; }

    @GetMapping
    public ApiResponse<Page<ProductCard>> list(@RequestParam(defaultValue = "") String q, @RequestParam(defaultValue = "0") long category,
                                               @RequestParam(defaultValue = "0") double minPrice, @RequestParam(defaultValue = "999999") double maxPrice,
                                               @RequestParam(defaultValue = "popular") String sort, @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "12") int size) {
        return ApiResponse.ok(service.search(q, category, minPrice, maxPrice, sort, page, size));
    }

    @GetMapping("/suggest")
    public ApiResponse<List<String>> suggest(@RequestParam String q) { return ApiResponse.ok(service.suggest(q)); }

    @GetMapping("/{id}")
    public ApiResponse<ProductDetail> detail(@PathVariable Long id) { return ApiResponse.ok(service.detail(id)); }
}
