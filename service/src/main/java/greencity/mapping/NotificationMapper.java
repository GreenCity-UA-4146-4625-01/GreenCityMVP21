package greencity.mapping;


import greencity.dto.notification.NewReplyNotificationDto;
import greencity.dto.notification.NotificationDto;
import greencity.entity.Notification;
import greencity.enums.NotificationType;


public class NotificationMapper {
    public static Notification mapToEntity(NotificationDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("NotificationDto is null");
        }

        if (dto instanceof NewReplyNotificationDto replyDto) {
            return new NewReplyNotificationDtoMapper().convert(replyDto);
        }

        throw new IllegalArgumentException("Unsupported NotificationDto type: " + dto.getClass());
    }
}

