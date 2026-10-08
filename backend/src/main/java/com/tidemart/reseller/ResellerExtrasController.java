package com.tidemart.reseller;

import com.tidemart.common.ApiResponse;
import com.tidemart.common.exception.ResourceNotFoundException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reseller")
public class ResellerExtrasController {
    private final JdbcTemplate jdbc;
    private final ResellerRepository resellers;

    public ResellerExtrasController(JdbcTemplate jdbc, ResellerRepository resellers) { this.jdbc = jdbc; this.resellers = resellers; }

    /** Level by delivered orders: Bronze, Silver from 5, Gold from 20. The board ranks resellers by margin earned. */
    @GetMapping("/leaderboard")
    public ApiResponse<Map<String, Object>> board(@AuthenticationPrincipal Long uid) {
        Reseller me = resellers.findByUserId(uid).orElseThrow(() -> new ResourceNotFoundException("Become a reseller first"));
        long mine = jdbc.queryForObject("select count(*) from orders where reseller_id = ? and status = 'DELIVERED'", Long.class, me.id);
        List<Map<String, Object>> top = jdbc.queryForList("select r.id as resellerId, coalesce(u.name, 'Reseller') as name, coalesce(sum(oi.reseller_margin * oi.qty), 0) as earned, count(distinct o.id) as orders "
                + "from resellers r join users u on u.id = r.user_id left join orders o on o.reseller_id = r.id and o.status = 'DELIVERED' left join order_items oi on oi.order_id = o.id group by r.id, u.name order by earned desc limit 10");
        return ApiResponse.ok(Map.of("level", mine >= 20 ? "Gold" : mine >= 5 ? "Silver" : "Bronze", "deliveredOrders", mine, "nextLevelAt", mine >= 20 ? 0 : mine >= 5 ? 20 : 5, "myResellerId", me.id, "top", top));
    }
}
