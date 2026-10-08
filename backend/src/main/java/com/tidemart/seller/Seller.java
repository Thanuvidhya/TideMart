package com.tidemart.seller;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sellers")
public class Seller {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public Long userId;
    public String businessName, gstNo, panNo, bankAccount, ifsc, status = "PENDING";
    public double rating;
}
