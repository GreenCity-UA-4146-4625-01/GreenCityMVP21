package greencity.mapping;

import greencity.dto.econewscomment.EcoNewsCommentDto;
import greencity.dto.notification.NewCommentNotificationDto;
import greencity.dto.user.UserVO;
import greencity.entity.Notification;
import greencity.enums.NotificationType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NewCommentNotificationDtoMapperTest {

    @InjectMocks
    private NewCommentNotificationDtoMapper mapper;

    @Test
    void convert_returnsNullIfNullPassed() {
        NewCommentNotificationDto dto = null;
        assertNull(mapper.convert(dto));
    }

    @Test
    void convert_returnsCorrectNotification() {
        NewCommentNotificationDto dto = NewCommentNotificationDto.builder()
                .isRead(true)
                .comment(EcoNewsCommentDto.builder()
                        .id(1L)
                        .build())
                .receiver(UserVO.builder()
                        .id(1L)
                        .build())
                .build();

        Notification entity = mapper.convert(dto);

        assertTrue(entity.isRead());
        assertEquals(NotificationType.NEW_COMMENT, entity.getType());
        assertEquals(1L, entity.getObjectId());
        assertEquals(1L, entity.getReceiver().getId());
    }
}
