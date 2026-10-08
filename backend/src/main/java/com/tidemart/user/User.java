package com.tidemart.user;

import com.tidemart.common.Role;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String phone, email, passwordHash, name, photoUrl, referralCode;
    public String language = "en";
    public String status = "ACTIVE";
    @Enumerated(EnumType.STRING)
    public Role role = Role.CUSTOMER;
    public LocalDateTime createdAt = LocalDateTime.now();
}
