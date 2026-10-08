package com.tidemart.coupon;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class CouponService {
    public record Result(double discount, String message) {}
    private final CouponRepository repo;

    public CouponService(CouponRepository repo) { this.repo = repo; }

    public Result apply(String code, double subtotal) {
        if (code == null || code.isBlank()) return new Result(0, null);
        Coupon c = repo.findByCodeIgnoreCase(code.trim()).orElse(null);
        LocalDateTime now = LocalDateTime.now();
        if (c == null || !c.active || (c.validTill != null && now.isAfter(c.validTill)) || (c.validFrom != null && now.isBefore(c.validFrom))) return new Result(0, "Invalid or expired coupon");
        if (subtotal < c.minOrder) return new Result(0, "Needs a cart of Rs " + (int) c.minOrder + " or more");
        double d = "PERCENT".equals(c.type) ? subtotal * c.value / 100 : c.value;
        if ("PERCENT".equals(c.type) && c.maxDiscount > 0) d = Math.min(d, c.maxDiscount);
        return new Result(Math.round(d), "Coupon " + c.code + " applied");
    }
}
