package greencity.dto.notification;

import com.fasterxml.jackson.annotation.JsonTypeName;
import greencity.dto.econewscomment.EcoNewsCommentDto;
import greencity.dto.user.UserVO;
import lombok.Builder;

@Builder
@JsonTypeName("NEW_COMMENT")
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
