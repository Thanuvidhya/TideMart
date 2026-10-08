package com.tidemart.delivery;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_attempts")
public class DeliveryAttempt {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public Long orderId, partnerId;
    public String status, note;
    public LocalDateTime createdAt = LocalDateTime.now();
}
