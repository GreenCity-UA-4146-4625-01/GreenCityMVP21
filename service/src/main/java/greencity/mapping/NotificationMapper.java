package greencity.mapping;


import greencity.dto.notification.NewReplyNotificationDto;
import greencity.dto.notification.NotificationDto;
import greencity.entity.Notification;


import greencity.util.NotificationTextFormatter;

public class NotificationMapper {
    public static Notification mapToEntity(NotificationDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("NotificationDto is null");
        }

        if (dto instanceof NewReplyNotificationDto replyDto) {
            Notification notification = new NewReplyNotificationDtoMapper().convert(replyDto);

            // додаємо згенерований текст
            String text = NotificationTextFormatter.formatNewReplyText(
                    replyDto.comment().getAuthor().getName(),
                    "news",
                    replyDto.comment().getEcoNewsTitle(),
                    replyDto.comment().getModifiedDate()
            );

            notification.setText(text);
            return notification;
        }

        throw new IllegalArgumentException("Unsupported NotificationDto type: " + dto.getClass());
    }
}
