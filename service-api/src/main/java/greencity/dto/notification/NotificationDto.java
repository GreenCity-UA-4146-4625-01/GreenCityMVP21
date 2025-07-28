package greencity.dto.notification;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import greencity.dto.user.UserVO;

/**
 * Base interface for all notification DTOs.
 */

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        visible = true
)
public interface NotificationDto {
    boolean isRead();
    UserVO receiver();
    long objectId();
    String text();
}
