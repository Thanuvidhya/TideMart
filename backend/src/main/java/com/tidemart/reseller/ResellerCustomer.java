package com.tidemart.reseller;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reseller_customers")
public class ResellerCustomer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public Long resellerId;
    public String name, phone, address;
}
