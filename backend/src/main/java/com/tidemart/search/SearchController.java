package com.tidemart.search;

import com.tidemart.common.ApiResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {
    private final SearchHistoryRepository repo;
    private final JdbcTemplate jdbc;

    public SearchController(SearchHistoryRepository repo, JdbcTemplate jdbc) { this.repo = repo; this.jdbc = jdbc; }

    @PostMapping("/history")
    public ApiResponse<Void> save(@AuthenticationPrincipal Long uid, @RequestParam String term) {
        String t = term.trim().toLowerCase();
        if (t.length() >= 2 && t.length() <= 100) { SearchHistory h = new SearchHistory(); h.userId = uid; h.term = t; repo.save(h); }
        return ApiResponse.ok(null);
    }

    @GetMapping("/history")
    public ApiResponse<List<String>> history(@AuthenticationPrincipal Long uid) { return ApiResponse.ok(repo.findTop20ByUserIdOrderByIdDesc(uid).stream().map(h -> h.term).distinct().limit(5).toList()); }

    @GetMapping("/trending")
    public ApiResponse<List<String>> trending() { return ApiResponse.ok(jdbc.queryForList("select term from search_history group by term order by count(*) desc limit 6", String.class)); }
}
