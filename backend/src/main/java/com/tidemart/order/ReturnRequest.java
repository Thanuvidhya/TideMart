package com.tidemart.order;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "return_requests")
public class ReturnRequest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public Long orderId, orderItemId;
    public String type, reason, photoUrls, status = "REQUESTED";
    public LocalDateTime createdAt = LocalDateTime.now();
}
