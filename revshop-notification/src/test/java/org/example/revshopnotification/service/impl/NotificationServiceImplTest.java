package org.example.revshopnotification.service.impl;

import org.example.revshopnotification.model.Notification;
import org.example.revshopnotification.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceImplTest {

    @Mock
    private NotificationRepository repo;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private Notification testNotification;



    @BeforeEach
    void setUp() {
        testNotification = new Notification(1, "Test Message", "INFO");
        testNotification.setCreatedAt(LocalDateTime.now());
        testNotification.setIsRead(false);
    }

    @Test
    void testNotifyUser_Success() {
        when(repo.save(any(Notification.class))).thenReturn(testNotification);

        Notification result = notificationService.notifyUser(1, "Test Message", "INFO");

        assertNotNull(result);
        assertEquals(1, result.getUserId());
        assertEquals("Test Message", result.getMessage());
        assertEquals("INFO", result.getType());
        verify(repo, times(1)).save(any(Notification.class));
    }

    @Test
    void testGetUserNotifications_Success() {
        when(repo.findByUserIdOrderByCreatedAtDesc(1)).thenReturn(Collections.singletonList(testNotification));

        List<Notification> result = notificationService.getUserNotifications(1);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Message", result.get(0).getMessage());
    }

}
