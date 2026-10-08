package com.tidemart.product;

import com.tidemart.common.ApiResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class QuestionController {
    private final QuestionService service;

    public QuestionController(QuestionService service) { this.service = service; }

    @GetMapping("/products/{id}/questions")
    public ApiResponse<List<QuestionService.QA>> list(@PathVariable Long id) { return ApiResponse.ok(service.list(id)); }

    @PostMapping("/products/{id}/questions")
    public ApiResponse<List<QuestionService.QA>> ask(@AuthenticationPrincipal Long uid, @PathVariable Long id, @RequestBody Map<String, String> body) { service.ask(uid, id, body.get("question")); return ApiResponse.ok(service.list(id)); }

    @PostMapping("/questions/{id}/answers")
    public ApiResponse<Void> answer(@AuthenticationPrincipal Long uid, @PathVariable Long id, @RequestBody Map<String, String> body) { service.answer(uid, id, body.get("answer")); return ApiResponse.ok(null); }
}
