package com.tidemart.product;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_variants")
public class ProductVariant {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public Long productId;
    public String size, color, sku;
    public int stock;
}
