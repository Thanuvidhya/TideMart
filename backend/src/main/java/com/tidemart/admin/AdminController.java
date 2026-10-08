package com.tidemart.admin;

import com.tidemart.common.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService s;

    public AdminController(AdminService s) { this.s = s; }

    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> stats() { return ApiResponse.ok(s.stats()); }

    @GetMapping("/table/{t}")
    public ApiResponse<List<Map<String, Object>>> list(@PathVariable String t) { return ApiResponse.ok(s.list(t)); }

    @PostMapping("/table/{t}")
    public ApiResponse<Void> create(@PathVariable String t, @RequestBody Map<String, Object> body) { s.create(t, body); return ApiResponse.ok(null); }

    @PutMapping("/table/{t}/{id}")
    public ApiResponse<Void> update(@PathVariable String t, @PathVariable Long id, @RequestBody Map<String, Object> body) { s.update(t, id, body); return ApiResponse.ok(null); }

    @GetMapping("/fraud")
    public ApiResponse<List<Map<String, Object>>> fraud() { return ApiResponse.ok(s.fraud()); }

    @PostMapping("/sellers/{id}/decision")
    public ApiResponse<Void> seller(@PathVariable Long id, @RequestParam boolean approve) { s.sellerDecision(id, approve); return ApiResponse.ok(null); }

    @PostMapping("/returns/{id}/status")
    public ApiResponse<Void> returnStatus(@PathVariable Long id, @RequestParam String to) { s.returnStatus(id, to); return ApiResponse.ok(null); }
}
