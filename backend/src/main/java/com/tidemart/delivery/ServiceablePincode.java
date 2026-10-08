package com.tidemart.delivery;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "serviceable_pincodes")
public class ServiceablePincode {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String pincode, city, state;
    public boolean codAvailable;
}
