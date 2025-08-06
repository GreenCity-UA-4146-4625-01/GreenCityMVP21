package greencity.dto.notification;

import com.fasterxml.jackson.annotation.JsonTypeName;
import greencity.dto.user.UserVO;
import lombok.Builder;

@Builder
@JsonTypeName("NEW_CANCELATION")
public record NewCancelationNotificationDto(
        boolean isRead,
        UserVO receiver,
        long eventId,
         String eventName,
        String text
) implements NotificationDto {

    @Override
    public long objectId() {return eventId;
    }


}
