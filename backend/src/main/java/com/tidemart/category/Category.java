package com.tidemart.category;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "categories")
public class Category {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public Long parentId;
    public String name, slug, imageUrl;
    public boolean active = true;
}
