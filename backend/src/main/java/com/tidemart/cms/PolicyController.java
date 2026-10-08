package com.tidemart.cms;

import com.tidemart.common.ApiResponse;
import com.tidemart.common.exception.ResourceNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/policies")
public class PolicyController {
    private final PolicyPageRepository repo;

    public PolicyController(PolicyPageRepository repo) { this.repo = repo; }

    @GetMapping("/{slug}")
    public ApiResponse<PolicyPage> get(@PathVariable String slug) { return ApiResponse.ok(repo.findBySlug(slug).orElseThrow(() -> new ResourceNotFoundException("Page not found"))); }
}
