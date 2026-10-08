package com.tidemart.notification;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public Long userId;
    public String title, body, type = "INFO";
    public boolean isRead;
    public LocalDateTime createdAt = LocalDateTime.now();
}
