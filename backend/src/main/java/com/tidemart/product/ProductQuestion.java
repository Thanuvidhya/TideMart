package com.tidemart.product;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_questions")
public class ProductQuestion {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public Long productId, userId;
    public String question;
    public LocalDateTime createdAt = LocalDateTime.now();
}
