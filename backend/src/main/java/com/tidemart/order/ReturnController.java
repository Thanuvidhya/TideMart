package com.tidemart.order;

import com.tidemart.common.ApiResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/returns")
public class ReturnController {
    private final ReturnService service;

    public ReturnController(ReturnService service) { this.service = service; }

    @PostMapping
    public ApiResponse<ReturnRequest> request(@AuthenticationPrincipal Long uid, @RequestBody ReturnService.Request r) { return ApiResponse.ok(service.request(uid, r)); }

    @GetMapping
    public ApiResponse<List<ReturnRequest>> list(@AuthenticationPrincipal Long uid) { return ApiResponse.ok(service.list(uid)); }

    @PostMapping("/{id}/advance")
    public ApiResponse<ReturnRequest> advance(@AuthenticationPrincipal Long uid, @PathVariable Long id) { return ApiResponse.ok(service.advance(uid, id)); }
}
