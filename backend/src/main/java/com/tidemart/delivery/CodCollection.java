package com.tidemart.delivery;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cod_collections")
public class CodCollection {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public Long orderId, partnerId;
    public double amount;
    public String status;
    public LocalDateTime collectedAt, settledAt;
}
