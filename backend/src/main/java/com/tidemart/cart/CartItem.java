package com.tidemart.cart;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cart_items")
public class CartItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public Long cartId, productId, variantId;
    public int qty;
    public boolean savedForLater;
}
