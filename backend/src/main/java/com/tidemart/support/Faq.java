package com.tidemart.support;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "faqs")
public class Faq {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String question, answer;
    public int sortOrder;
}
