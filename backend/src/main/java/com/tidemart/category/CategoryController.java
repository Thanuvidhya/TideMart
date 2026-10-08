package com.tidemart.category;

import com.tidemart.common.ApiResponse;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryRepository repo;

    public CategoryController(CategoryRepository repo) { this.repo = repo; }

    @GetMapping
    public ApiResponse<List<Category>> list() { return ApiResponse.ok(repo.findByActiveTrueOrderByIdAsc()); }
}
