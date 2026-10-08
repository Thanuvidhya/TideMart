package com.tidemart.product;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_media")
public class ProductMedia {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public Long productId;
    public String url, type = "IMAGE";
    public int sortOrder;
}
