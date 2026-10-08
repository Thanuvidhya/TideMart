package com.tidemart.order;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "Orders")
@Table(name = "orders")
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String orderNo, shipName, shipPhone, shipLine1, shipCity, shipState, shipPincode, paymentMethod, paymentStatus, status = "PLACED";
    public Long userId, resellerId, couponId;
    public double subtotal, discount, deliveryFee, codFee, total;
    public LocalDateTime createdAt = LocalDateTime.now();
}
