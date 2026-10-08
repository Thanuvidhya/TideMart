package com.tidemart.product;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public Long sellerId, categoryId;
    public String name, description, status = "APPROVED";
    public double price, mrp, ratingAvg;
    public int ratingCount;
    public LocalDateTime createdAt = LocalDateTime.now();
}
