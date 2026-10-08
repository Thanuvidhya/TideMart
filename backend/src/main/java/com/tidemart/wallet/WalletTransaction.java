package com.tidemart.wallet;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallet_transactions")
public class WalletTransaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public Long walletId;
    public String type, reason;
    public double amount;
    public LocalDateTime createdAt = LocalDateTime.now();
}
