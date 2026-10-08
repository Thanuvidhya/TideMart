package com.tidemart.delivery;

import com.tidemart.common.ApiResponse;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/pincode")
public class PincodeController {
    private final ServiceablePincodeRepository repo;

    public PincodeController(ServiceablePincodeRepository repo) { this.repo = repo; }

    @GetMapping("/{pin}/check")
    public ApiResponse<Map<String, Object>> check(@PathVariable String pin) {
        if (pin == null || !pin.matches("\\d{6}")) return ApiResponse.ok(Map.of("serviceable", false, "codAvailable", false, "deliveryBy", LocalDate.now().plusDays(3).toString()));
        var p = repo.findByPincode(pin);
        return ApiResponse.ok(Map.of("serviceable", true, "codAvailable", p.map(x -> x.codAvailable).orElse(true), "deliveryBy", LocalDate.now().plusDays(3).toString()));
    }
}
