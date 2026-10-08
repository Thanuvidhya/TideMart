package com.tidemart.seller;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pickup_addresses")
public class PickupAddress {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public Long sellerId;
    public String line1, city, state, pincode;
}
