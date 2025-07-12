package greencity.service;

import greencity.dto.notification.NotificationInternalDto;
import greencity.dto.notification.NotificationDto;

import java.util.List;

public interface NotificationService {

    /**
     * Create new notification by event
     */
    void createNotification(NotificationInternalDto event);

    /**
     * Get all notification for user
     */
    List<NotificationDto> getAllNotificationsForUser(Long userId);

    /**
     * Mark notification as read
     */
    void markAsRead(List<Long> notificationIds);

    /**
     * Mark all notification as read
     */
    void markAllAsRead(Long userId);

    /**
     * Get count unread notification
     */
    int countUnreadNotifications(Long userId);
}

