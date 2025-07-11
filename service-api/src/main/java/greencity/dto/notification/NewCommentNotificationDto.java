package greencity.dto.notification;

import greencity.dto.econewscomment.EcoNewsCommentDto;
import greencity.dto.user.UserVO;
import lombok.Builder;

@Builder
public record NewCommentNotificationDto(
    boolean isRead,
    UserVO receiver,
    EcoNewsCommentDto comment
) implements NotificationDto {

    @Override
    public long objectId() {
        return comment.getId();
    }
}
