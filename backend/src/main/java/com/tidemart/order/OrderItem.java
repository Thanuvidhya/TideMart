package com.tidemart.order;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public Long orderId, productId, variantId, sellerId;
    public int qty;
    public double unitPrice, resellerMargin, commission;
}
