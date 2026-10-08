package com.tidemart.coupon;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
public class Coupon {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String code, type;
    public double value, maxDiscount, minOrder;
    public LocalDateTime validFrom, validTill;
    public int usageLimit;
    public boolean active;
}
