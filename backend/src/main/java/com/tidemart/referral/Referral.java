package com.tidemart.referral;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "referrals")
public class Referral {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public Long referrerId, referredId;
    public double reward;
    public String status;
}
