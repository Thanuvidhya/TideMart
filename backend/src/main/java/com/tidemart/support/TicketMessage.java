package com.tidemart.support;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ticket_messages")
public class TicketMessage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public Long ticketId, senderId;
    public String message;
    public LocalDateTime createdAt = LocalDateTime.now();
}
