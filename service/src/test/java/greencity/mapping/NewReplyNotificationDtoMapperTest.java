package greencity.mapping;

import greencity.dto.econewscomment.EcoNewsCommentDto;
import greencity.dto.notification.NewReplyNotificationDto;
import greencity.dto.user.UserVO;
import greencity.entity.Notification;
import greencity.enums.NotificationType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NewReplyNotificationDtoMapperTest {

    @InjectMocks
    private NewReplyNotificationDtoMapper mapper;

    @Test
    void convert_returnsNullWhenPassedNull() {
        NewReplyNotificationDto dto = null;
        assertNull(mapper.convert(dto));
    }

    @Test
    void convert_returnsCorrectNotification() {
        NewReplyNotificationDto dto = NewReplyNotificationDto.builder()
                .isRead(true)
                .comment(EcoNewsCommentDto.builder()
                        .id(1L)
                        .build())
                .parentComment(EcoNewsCommentDto.builder()
                        .id(2L)
                        .build())
                .receiver(UserVO.builder()
                        .id(1L)
                        .build())
                .build();

        Notification notification = mapper.convert(dto);
        assertTrue(notification.isRead());
        assertEquals(1L, notification.getObjectId());
        assertEquals(1L, notification.getReceiver().getId());
        assertEquals(NotificationType.NEW_REPLY, notification.getType());
    }
}
