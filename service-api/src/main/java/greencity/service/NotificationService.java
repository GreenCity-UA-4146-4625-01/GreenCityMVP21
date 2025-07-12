package greencity.service;

import greencity.dto.notification.NotificationInternalDto;
import greencity.dto.notification.NotificationDto;

import java.util.List;

public interface NotificationService {

    public interface NotificationService {

        /**
         * Creates a new notification based on the internal DTO.
         *
         * @param dto DTO containing data for creating a notification (type, object, receiver)
         */
        void createNewCommentNotification(NewCommentNotificationDto dto);
        void createNewReplyNotification(NewReplyNotificationDto dto);
        void createNewLikeNotification(NewLikeNotificationDto dto);

        /**
         * Retrieves all notifications for a given user.
         * Results are typically sorted with the newest notifications first.
         *
         * @param userId ID of the user
         * @return List of notification DTOs for UI display
         */
        List<NotificationDto> getAllNotificationsForUser(Long userId);

        /**
         * Marks a specific notification as read.
         *
         * @param notificationId ID of the notification
         */
        void markAsRead(Long notificationId);

        /**
         * Marks a list of notifications as read.
         * Useful for actions like "Mark all as read" button.
         *
         * @param notificationIds List of notification IDs
         */
        void markAsReadBulk(List<Long> notificationIds);

        /**
         * Marks all notifications of a user as read.
         *
         * @param userId ID of the user
         */
        void markAllAsRead(Long userId);

        /**
         * Deletes a notification by its ID.
         *
         * @param notificationId ID of the notification
         */
        void deleteNotification(Long notificationId);

        /**
         * Deletes multiple notifications by their IDs.
         * Useful for bulk deletion or clearing notifications.
         *
         * @param notificationIds List of notification IDs
         */
    }
}

