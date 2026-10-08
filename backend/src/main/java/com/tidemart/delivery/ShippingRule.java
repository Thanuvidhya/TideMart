package com.tidemart.delivery;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "shipping_rules")
public class ShippingRule {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public double minOrder, maxOrder, fee;
    public boolean active;
}
