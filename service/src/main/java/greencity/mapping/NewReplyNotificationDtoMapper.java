package greencity.mapping;

import greencity.dto.notification.NewReplyNotificationDto;
import greencity.entity.Notification;
import greencity.entity.User;
import greencity.enums.NotificationType;
import org.modelmapper.AbstractConverter;

public class NewReplyNotificationDtoMapper extends AbstractConverter<NewReplyNotificationDto, Notification> {
    @Override
    protected Notification convert(NewReplyNotificationDto dto) {
        if (dto == null) return null;

        return Notification.builder()
                .isRead(false)
                .type(NotificationType.NEW_REPLY)
                .objectId(dto.objectId())
                .receiver(User.builder()
                        .id(dto.receiver().getId())
                        .build())
                .build();
    }
}
