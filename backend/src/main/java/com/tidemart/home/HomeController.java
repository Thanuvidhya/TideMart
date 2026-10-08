package com.tidemart.home;

import com.tidemart.category.CategoryRepository;
import com.tidemart.common.ApiResponse;
import com.tidemart.product.ProductService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;

@RestController
@RequestMapping("/api/home")
public class HomeController {
    private final CategoryRepository categories;
    private final ProductService products;
    private final JdbcTemplate jdbc;

    public HomeController(CategoryRepository categories, ProductService products, JdbcTemplate jdbc) { this.categories = categories; this.products = products; this.jdbc = jdbc; }

    @GetMapping
    public ApiResponse<Map<String, Object>> home() {
        Map<String, Object> m = new HashMap<>();
        m.put("categories", categories.findByActiveTrueOrderByIdAsc());
        m.put("trending", products.trending());
        m.put("banners", jdbc.queryForList("select title, image_url as imageUrl, link from banners where active = true order by sort_order"));
        Map<String, Object> categoryProducts = new LinkedHashMap<>();
        categories.findByActiveTrueOrderByIdAsc().forEach(c -> categoryProducts.put(c.name, products.categoryProducts(c.id)));
        m.put("categoryProducts", categoryProducts);
        return ApiResponse.ok(m);
    }
}
