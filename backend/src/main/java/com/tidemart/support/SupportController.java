package com.tidemart.support;

import com.tidemart.common.ApiResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SupportController {
    private final SupportService s;
    private final FaqRepository faqs;

    public SupportController(SupportService s, FaqRepository faqs) { this.s = s; this.faqs = faqs; }

    @GetMapping("/faqs")
    public ApiResponse<List<Faq>> faqs() { return ApiResponse.ok(faqs.findAllByOrderBySortOrderAsc()); }

    @PostMapping("/support/tickets")
    public ApiResponse<SupportTicket> create(@AuthenticationPrincipal Long uid, @RequestBody SupportService.TicketRequest r) { return ApiResponse.ok(s.create(uid, r)); }

    @GetMapping("/support/tickets")
    public ApiResponse<List<SupportTicket>> list(@AuthenticationPrincipal Long uid) { return ApiResponse.ok(s.list(uid)); }

    @GetMapping("/support/tickets/{id}")
    public ApiResponse<SupportService.TicketView> view(@AuthenticationPrincipal Long uid, @PathVariable Long id) { return ApiResponse.ok(s.view(uid, id)); }

    @PostMapping("/support/tickets/{id}/messages")
    public ApiResponse<SupportService.TicketView> reply(@AuthenticationPrincipal Long uid, @PathVariable Long id, @RequestBody Map<String, String> body) { return ApiResponse.ok(s.reply(uid, id, body.get("message"))); }

    @GetMapping("/support/chat")
    public ApiResponse<List<ChatMessage>> chat(@AuthenticationPrincipal Long uid) { return ApiResponse.ok(s.chat(uid)); }

    @PostMapping("/support/chat")
    public ApiResponse<List<ChatMessage>> send(@AuthenticationPrincipal Long uid, @RequestBody Map<String, String> body) { return ApiResponse.ok(s.send(uid, body.get("message"))); }
}
