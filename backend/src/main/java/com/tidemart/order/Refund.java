package com.tidemart.order;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "refunds")
public class Refund {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public Long orderId, returnId;
    public double amount;
    public String method, status;
    public LocalDateTime createdAt = LocalDateTime.now();
}
