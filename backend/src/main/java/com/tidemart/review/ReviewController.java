package com.tidemart.review;

import com.tidemart.common.ApiResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ReviewController {
    private final ReviewService service;

    public ReviewController(ReviewService service) { this.service = service; }

    @GetMapping("/products/{id}/reviews")
    public ApiResponse<List<ReviewService.View>> list(@PathVariable Long id) { return ApiResponse.ok(service.list(id)); }

    @PostMapping("/reviews")
    public ApiResponse<Review> add(@AuthenticationPrincipal Long uid, @RequestBody ReviewService.Request r) { return ApiResponse.ok(service.add(uid, r)); }
}
