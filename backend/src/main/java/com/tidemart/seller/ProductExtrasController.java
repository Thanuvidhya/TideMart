package com.tidemart.seller;

import com.tidemart.common.ApiResponse;
import com.tidemart.common.exception.BadRequestException;
import com.tidemart.common.exception.ResourceNotFoundException;
import com.tidemart.product.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/** Size chart, extra photos or video, and promoted listings. Promotions are free in this demo. */
@RestController
@RequestMapping("/api")
public class ProductExtrasController {
    private final SellerRepository sellers;
    private final ProductRepository products;
    private final SizeChartRepository charts;
    private final ProductMediaRepository media;
    private final JdbcTemplate jdbc;

    public ProductExtrasController(SellerRepository sellers, ProductRepository products, SizeChartRepository charts, ProductMediaRepository media, JdbcTemplate jdbc) {
        this.sellers = sellers; this.products = products; this.charts = charts; this.media = media; this.jdbc = jdbc;
    }

    @GetMapping("/products/{id}/size-chart")
    public ApiResponse<Map<String, String>> chart(@PathVariable Long id) { return ApiResponse.ok(Map.of("chart", charts.findByProductId(id).map(c -> c.chartJson == null ? "" : c.chartJson).orElse(""))); }

    /** Chart text looks like: Size,Chest,Waist;S,36,30;M,38,32 */
    @PutMapping("/seller/products/{id}/size-chart")
    public ApiResponse<Void> saveChart(@AuthenticationPrincipal Long uid, @PathVariable Long id, @RequestBody Map<String, String> body) {
        own(uid, id);
        SizeChart c = charts.findByProductId(id).orElseGet(SizeChart::new);
        c.productId = id; c.chartJson = body.get("chart");
        charts.save(c);
        return ApiResponse.ok(null);
    }

    @PostMapping("/seller/products/{id}/media")
    public ApiResponse<Void> addMedia(@AuthenticationPrincipal Long uid, @PathVariable Long id, @RequestBody Map<String, String> body) {
        own(uid, id);
        if (body.get("url") == null || body.get("url").isBlank()) throw new BadRequestException("Upload a file first");
        ProductMedia m = new ProductMedia();
        m.productId = id; m.url = body.get("url"); m.type = "VIDEO".equals(body.get("type")) ? "VIDEO" : "IMAGE"; m.sortOrder = 50;
        media.save(m);
        return ApiResponse.ok(null);
    }

    @PostMapping("/seller/products/{id}/promote")
    public ApiResponse<Void> promote(@AuthenticationPrincipal Long uid, @PathVariable Long id, @RequestParam(defaultValue = "7") int days) {
        Seller s = own(uid, id);
        if (days < 1 || days > 30) throw new BadRequestException("Choose 1 to 30 days");
        jdbc.update("insert into product_promotions (product_id, seller_id, ends_at) values (?, ?, date_add(now(), interval ? day))", id, s.id, days);
        return ApiResponse.ok(null);
    }

    private Seller own(Long uid, Long productId) {
        Seller s = sellers.findByUserId(uid).orElseThrow(() -> new ResourceNotFoundException("Create a seller account first"));
        products.findById(productId).filter(p -> s.id.equals(p.sellerId)).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return s;
    }
}
