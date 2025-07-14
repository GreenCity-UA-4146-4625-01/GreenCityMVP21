package greencity.mapping;

import greencity.dto.notification.NewCommentNotificationDto;
import greencity.entity.Notification;
import greencity.entity.User;
import greencity.enums.NotificationType;
import org.modelmapper.AbstractConverter;

public class NewCommentNotificationDtoMapper extends AbstractConverter<NewCommentNotificationDto, Notification> {
    @Override
    protected Notification convert(NewCommentNotificationDto dto) {
        if (dto == null) return null;

        return Notification.builder()
                .isRead(false)
                .type(NotificationType.NEW_COMMENT)
                .objectId(dto.objectId())
                .receiver(User.builder()
                        .id(dto.receiver().getId())
                        .build())
                .build();
    }
}
