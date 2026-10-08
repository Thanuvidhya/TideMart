package com.tidemart.reseller;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reseller_products")
public class ResellerProduct {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public Long resellerId, productId;
    public double margin;
}
