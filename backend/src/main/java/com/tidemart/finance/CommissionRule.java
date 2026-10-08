package com.tidemart.finance;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "commission_rules")
public class CommissionRule {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public Long categoryId;
    public double percent;
}
