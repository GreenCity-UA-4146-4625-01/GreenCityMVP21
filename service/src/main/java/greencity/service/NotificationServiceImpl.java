package greencity.service;

import greencity.dto.notification.*;
import greencity.entity.Notification;
import greencity.entity.User;
import greencity.enums.NotificationType;
import greencity.repository.NotificationRepo;
import greencity.repository.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service implementation for managing user notifications.
 */
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepo notificationRepo;
    private final UserRepo userRepository;
    private final ModelMapper modelMapper;


    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }

    /**
     * Creates a notification when a new comment is added.
     *
     * @param dto DTO with info about the new comment notification
     */
    @Override
    public void createNotification(NotificationDto dto) {
        Notification notification = modelMapper.convert(dto, Notification.class);
        notification.setReceiver(findUserById(dto.receiver().getId()));
        notificationRepo.save(notification);
    }

    /**
     * Retrieves all notifications for a specific user.
     *
     * @param userId ID of the user
     * @return List of notification DTOs
     */
    @Override
    public List<NotificationDto> getAllNotificationsForUser(Long userId) {
        User receiver = findUserById(userId);

        return notificationRepo.findAllByReceiver(receiver).stream()
                .map(notification -> modelMapper.map(notification, NotificationDto.class))
                .toList();
    }

    /**
     * Marks a notification as read.
     *
     * @param notificationId ID of the notification to mark
     */
    @Override
    public void markAsRead(Long notificationId) {
        notificationRepo.findById(notificationId).ifPresent(notification -> {
            notification.setRead(true);
            notificationRepo.save(notification);
        });
    }

    /**
     * Bulk mark notifications as read.
     *
     * @param ids list of notification IDs to mark
     */
    @Override
    public void markAsReadBulk(List<Long> ids) {
        List<Notification> notifications = notificationRepo.findAllById(ids);
        notifications.forEach(n -> n.setRead(true));
        notificationRepo.saveAll(notifications);
    }

    /**
     * Marks all notifications for a user as read.
     *
     * @param userId ID of the user
     */
    @Override
    public void markAllAsRead(Long userId) {
        User receiver = findUserById(userId);
        List<Notification> unread = notificationRepo.findUnreadByReceiver(receiver);
        unread.forEach(notification -> notification.setRead(true));
        notificationRepo.saveAll(unread);
    }

    /**
     * Deletes a notification by ID.
     *
     * @param notificationId ID of the notification to delete
     */
    @Override
    public void deleteNotification(Long notificationId) {
        notificationRepo.deleteById(notificationId);
    }



    @Override
    public void createReplyNotification(NewReplyNotificationDto dto) {
        if (dto.receiver() == null || dto.receiver().getId() == null) {
            throw new IllegalArgumentException("Receiver must not be null");
        }

        Notification notification = Notification.builder()
                .receiver(findUserById(dto.receiver().getId()))
                .objectId(dto.objectId())
                .type(NotificationType.NEW_REPLY)
                .isRead(false)
                .build();

        notificationRepo.save(notification);
    }


}
