package org.example.revshopnotification.service.impl;


import org.example.revshopnotification.model.Notification;
import org.example.revshopnotification.repository.NotificationRepository;
import org.example.revshopnotification.service.NotificationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repo;

    public NotificationServiceImpl(NotificationRepository repo) {
        this.repo = repo;
    }

    @Override
    public Notification notifyUser(Integer userId, String message, String type) {
        Notification n = new Notification(userId, message, type);
        n.setCreatedAt(LocalDateTime.now());
        n.setIsRead(false);

        return repo.save(n);
    }

    @Override
    public List<Notification> getUserNotifications(Integer userId) {
        return repo.findByUserIdOrderByCreatedAtDesc(userId);
    }
}