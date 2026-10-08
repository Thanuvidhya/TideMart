package com.tidemart.auth;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "otp_tokens")
public class OtpToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String phone, code;
    public LocalDateTime expiresAt;
    public boolean used;
}
