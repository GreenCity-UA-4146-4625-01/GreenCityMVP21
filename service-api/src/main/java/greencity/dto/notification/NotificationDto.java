package greencity.dto.notification;

import greencity.dto.user.UserVO;

/**
 * Base interface for all notification DTOs.
 */
public interface NotificationDto {
    boolean isRead();
    UserVO receiver();
    long objectId();
}
