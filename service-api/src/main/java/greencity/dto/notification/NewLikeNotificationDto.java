package greencity.dto.notification;

import com.fasterxml.jackson.annotation.JsonTypeName;
import greencity.dto.user.UserVO;
import lombok.Builder;

@Builder
@JsonTypeName("NEW_LIKE")
public record NewLikeNotificationDto(
        boolean isRead,
        UserVO receiver,
        UserVO lUser,
        Long eventId,
        String text
)implements NotificationDto{
    @Override
    public long objectId() {return eventId;
    }
}
