package com.tidemart.support;

import com.tidemart.common.exception.BadRequestException;
import com.tidemart.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SupportService {
    public record TicketRequest(String subject, String message, Long orderId) {}
    public record TicketView(SupportTicket ticket, List<TicketMessage> messages) {}
    private final SupportTicketRepository tickets;
    private final TicketMessageRepository messages;
    private final ChatMessageRepository chats;

    public SupportService(SupportTicketRepository tickets, TicketMessageRepository messages, ChatMessageRepository chats) { this.tickets = tickets; this.messages = messages; this.chats = chats; }

    @Transactional
    public SupportTicket create(Long uid, TicketRequest r) {
        if (r.subject() == null || r.subject().isBlank() || r.message() == null || r.message().isBlank()) throw new BadRequestException("Enter a subject and a message");
        SupportTicket t = new SupportTicket();
        t.userId = uid; t.orderId = r.orderId(); t.subject = r.subject();
        t = tickets.save(t);
        add(t.id, uid, r.message());
        return t;
    }

    public List<SupportTicket> list(Long uid) { return tickets.findByUserIdOrderByIdDesc(uid); }

    public TicketView view(Long uid, Long id) { SupportTicket t = own(uid, id); return new TicketView(t, messages.findByTicketIdOrderByIdAsc(id)); }

    public TicketView reply(Long uid, Long id, String message) {
        if (message == null || message.isBlank()) throw new BadRequestException("Write a message");
        own(uid, id);
        add(id, uid, message);
        return view(uid, id);
    }

    public List<ChatMessage> chat(Long uid) { return chats.findByUserIdOrderByIdAsc(uid); }

    /** Demo chat: a simple keyword bot answers. A real agent inbox can replace it later. */
    public List<ChatMessage> send(Long uid, String text) {
        if (text == null || text.isBlank()) throw new BadRequestException("Write a message");
        save(uid, "USER", text);
        String m = text.toLowerCase(), a;
        if (m.contains("refund") || m.contains("return")) a = "Open My returns to follow a return. Refunds go to your wallet after pickup.";
        else if (m.contains("track") || m.contains("where") || m.contains("order")) a = "Open My orders and tap the order to see the live tracking timeline.";
        else if (m.contains("cancel")) a = "You can cancel an order from its page until it is shipped.";
        else if (m.contains("pay") || m.contains("upi") || m.contains("cod")) a = "We accept UPI and card (demo) and cash on delivery.";
        else a = "Thanks for writing. Please raise a ticket from the Help page and our team will reply.";
        save(uid, "SUPPORT", a);
        return chat(uid);
    }

    private void add(Long ticketId, Long uid, String message) { TicketMessage m = new TicketMessage(); m.ticketId = ticketId; m.senderId = uid; m.message = message; messages.save(m); }

    private void save(Long uid, String role, String text) { ChatMessage c = new ChatMessage(); c.userId = uid; c.senderRole = role; c.message = text; chats.save(c); }

    private SupportTicket own(Long uid, Long id) { return tickets.findById(id).filter(t -> t.userId.equals(uid)).orElseThrow(() -> new ResourceNotFoundException("Ticket not found")); }
}
