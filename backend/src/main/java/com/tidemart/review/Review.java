package com.tidemart.review;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
public class Review {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public Long productId, userId, orderId;
    public int rating;
    public String comment;
    public LocalDateTime createdAt = LocalDateTime.now();
}
