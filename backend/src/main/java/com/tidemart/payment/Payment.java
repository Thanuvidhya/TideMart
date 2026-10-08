package com.tidemart.payment;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public Long orderId;
    public String method, status, txnRef;
    public double amount;
    public LocalDateTime createdAt = LocalDateTime.now();
}
