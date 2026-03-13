package org.example.revshopnotification.service;



import org.example.revshopnotification.model.Notification;

import java.util.List;

public interface NotificationService {

    Notification notifyUser(Integer userId, String message, String type);

    List<Notification> getUserNotifications(Integer userId);
}