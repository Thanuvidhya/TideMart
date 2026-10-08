package com.tidemart.support;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "support_tickets")
public class SupportTicket {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public Long userId, orderId;
    public String subject, status = "OPEN";
    public LocalDateTime createdAt = LocalDateTime.now();
}
