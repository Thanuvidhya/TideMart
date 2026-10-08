package com.tidemart.admin;

import com.tidemart.common.ApiResponse;
import com.tidemart.common.exception.ResourceNotFoundException;
import com.tidemart.notification.NotificationService;
import com.tidemart.support.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/tickets")
public class AdminSupportController {
    private final SupportTicketRepository tickets;
    private final TicketMessageRepository messages;
    private final NotificationService notifier;

    public AdminSupportController(SupportTicketRepository tickets, TicketMessageRepository messages, NotificationService notifier) { this.tickets = tickets; this.messages = messages; this.notifier = notifier; }

    @GetMapping("/{id}")
    public ApiResponse<List<TicketMessage>> thread(@PathVariable Long id) { return ApiResponse.ok(messages.findByTicketIdOrderByIdAsc(id)); }

    @PostMapping("/{id}/reply")
    public ApiResponse<Void> reply(@AuthenticationPrincipal Long adminId, @PathVariable Long id, @RequestBody Map<String, String> body) {
        SupportTicket t = tickets.findById(id).orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        TicketMessage m = new TicketMessage();
        m.ticketId = id; m.senderId = adminId; m.message = "Support: " + body.get("message");
        messages.save(m);
        t.status = "IN_PROGRESS";
        tickets.save(t);
        notifier.notify(t.userId, "Support replied", "We replied to your ticket: " + t.subject);
        return ApiResponse.ok(null);
    }
}
