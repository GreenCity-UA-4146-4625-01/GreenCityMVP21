package greencity.service;

import greencity.dto.notification.NewCommentNotificationDto;
import greencity.dto.notification.NewLikeNotificationDto;
import greencity.dto.notification.NewReplyNotificationDto;
import greencity.dto.notification.NotificationDto;
import greencity.entity.Notification;
import greencity.entity.User;
import greencity.enums.NotificationType;
import greencity.repository.NotificationRepo;
import greencity.repository.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepo notificationRepo;
    private final UserRepo userRepository;
    private final ModelMapper modelMapper;

    public NotificationServiceImpl(NotificationRepo notificationRepo,
                                   UserRepo userRepository,
                                   ModelMapper modelMapper) {
        this.notificationRepo = notificationRepo;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public void createNewCommentNotification(NewCommentNotificationDto dto) {
        createNotification(dto.receiver().getId(), NotificationType.NEW_COMMENT, dto.objectId());
    }

    @Override
    public void createNewReplyNotification(NewReplyNotificationDto dto) {
        createNotification(dto.receiver().getId(), NotificationType.NEW_REPLY, dto.objectId());
    }

    @Override
    public void createNewLikeNotification(NewLikeNotificationDto dto) {
        createNotification(dto.receiver().getId(), NotificationType.NEW_LIKE, dto.objectId());
    }

    private void createNotification(Long receiverId, NotificationType type, long objectId) {
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Notification notification = Notification.builder()
                .type(type)
                .objectId(objectId)
                .receiver(receiver)
                .isRead(false)
                .build();

        notificationRepo.save(notification);
    }

    @Override
    public List<NotificationDto> getAllNotificationsForUser(Long userId) {
        User receiver = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return notificationRepo.findAllByReceiver(receiver).stream()
                .map(notification -> modelMapper.map(notification, NotificationDto.class))
                .toList();
    }

    @Override
    public void markAsRead(Long notificationId) {
        notificationRepo.findById(notificationId).ifPresent(notification -> {
            notification.setRead(true);
            notificationRepo.save(notification);
        });
    }

    @Override
    public void markAllAsRead(Long userId) {
        User receiver = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<Notification> unread = notificationRepo.findUnreadByReceiver(receiver);
        unread.forEach(notification -> notification.setRead(true));
        notificationRepo.saveAll(unread);
    }

    @Override
    public void deleteNotification(Long notificationId) {
        notificationRepo.deleteById(notificationId);
    }
}
