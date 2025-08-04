package greencity.dto.notification;

import greencity.dto.user.UserVO;
import greencity.enums.NotificationType;
import lombok.Builder;

@Builder
public record NewsLikedNotificationDto(
        UserVO receiver,
        String likerName,
        String newsTitle,
        long objectId,
        boolean isRead
) implements NotificationDto {

    public NotificationType type() {
        return NotificationType.NEWS_LIKED;
    }

    @Override
    public String text() {
        return String.format("%s likes your news %s.", this.likerName, this.newsTitle);
    }
}
