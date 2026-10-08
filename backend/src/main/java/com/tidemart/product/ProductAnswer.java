package com.tidemart.product;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_answers")
public class ProductAnswer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public Long questionId, userId;
    public String answer;
    public LocalDateTime createdAt = LocalDateTime.now();
}
