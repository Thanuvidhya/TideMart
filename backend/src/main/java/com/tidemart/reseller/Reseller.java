package com.tidemart.reseller;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resellers")
public class Reseller {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public Long userId;
    public String upiId, bankAccount, ifsc;
    public int level = 1;
}
