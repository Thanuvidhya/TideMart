package com.tidemart.notification;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    public record View(long unread, List<Notification> items) {}
    private final NotificationRepository repo;

    public NotificationService(NotificationRepository repo) { this.repo = repo; }

    /** In-app notifications only. No push or SMS provider is used. */
    public void notify(Long uid, String title, String body) {
        Notification n = new Notification();
        n.userId = uid; n.title = title; n.body = body;
        repo.save(n);
    }

    public View view(Long uid) { return new View(repo.countByUserIdAndIsReadFalse(uid), repo.findTop30ByUserIdOrderByIdDesc(uid)); }

    public void readAll(Long uid) { repo.findTop30ByUserIdOrderByIdDesc(uid).forEach(n -> { if (!n.isRead) { n.isRead = true; repo.save(n); } }); }
}
