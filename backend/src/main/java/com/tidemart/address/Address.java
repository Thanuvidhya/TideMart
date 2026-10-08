package com.tidemart.address;

import jakarta.persistence.*;

@Entity
@Table(name = "addresses")
public class Address {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public Long userId;
    public String name, phone, line1, city, state, pincode;
    public boolean isDefault;
}
