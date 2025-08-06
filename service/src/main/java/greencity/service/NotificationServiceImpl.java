package greencity.service;

import greencity.dto.notification.NotificationDto;
import greencity.entity.Notification;
import greencity.entity.User;
import greencity.mapping.NotificationMapper;
import greencity.repository.NotificationRepo;
import greencity.repository.UserRepo;
import greencity.sse.StreamingSubscription;
import greencity.sse.SubscriptionHolder;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

/**NewReplyNotificationDto
 * Service implementation for managing user notifications.
 */
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepo notificationRepo;
    private final UserRepo userRepository;
    private final ModelMapper modelMapper;

    private final SubscriptionHolder<NotificationDto> subscriptions;

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
        Notification notification = NotificationMapper.mapToEntity(dto);
        notification.setReceiver(findUserById(dto.receiver().getId()));
        notification.setCreationDate(ZonedDateTime.now());
        notificationRepo.save(notification);
        incrementNotificationCounter(dto.receiver().getId());
        subscriptions.notifyByUser(dto.receiver().getId(), dto);
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
    public int countUnreadNotifications(Long userId) {
        User receiver = findUserById(userId);
        return notificationRepo.countByReceiverAndIsReadFalse(receiver);
    }

    @Override
    public boolean isNotificationForUser(Long notificationId, Long userId) {
        return notificationRepo.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found with id: " + notificationId))
                .getReceiver().getId()
                .equals(userId);
    }

    @Override
    public StreamingSubscription<NotificationDto> subscribeForUser(Long userId) {
        return subscriptions.createForUser(userId);
    }

    @Override
    public void incrementNotificationCounter(Long userId){
        User reciever=findUserById(userId);
        reciever.setUnreadNotifications(reciever.getUnreadNotifications()+1);
        userRepository.save(reciever);
    }
}
