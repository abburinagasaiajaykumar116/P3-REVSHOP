package org.example.revshopnotification.controller;


import org.example.revshopnotification.model.Notification;
import org.example.revshopnotification.service.NotificationService;
import org.example.revshopnotification.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtUtil jwtUtil;

    public NotificationController(NotificationService notificationService, JwtUtil jwtUtil) {
        this.notificationService = notificationService;
        this.jwtUtil = jwtUtil;
    }

    // Send notification
    @PostMapping("/send")
    public ResponseEntity<?> sendNotification(@RequestHeader("Authorization") String authHeader,
                                              @RequestParam Integer targetUserId,
                                              @RequestParam String message,
                                              @RequestParam String type) {
        Integer senderId = jwtUtil.extractUserIdFromHeader(authHeader);
        if (senderId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
        }
        
        Notification notification = notificationService.notifyUser(targetUserId, message, type);
        return ResponseEntity.ok(notification);
    }

    // Get notifications for the logged-in user
    @GetMapping("/user")
    public ResponseEntity<?> getUserNotifications(@RequestHeader("Authorization") String authHeader) {
        Integer userId = jwtUtil.extractUserIdFromHeader(authHeader);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid Token"));
        }
        
        return ResponseEntity.ok(notificationService.getUserNotifications(userId));
    }
}