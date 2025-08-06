package greencity.listeners;

import greencity.dto.notification.NewLikeNotificationDto;
import greencity.dto.user.UserVO;
import greencity.entity.Event;
import greencity.entity.User;
import greencity.formatter.NotificationFormatter;
import greencity.repository.UserRepo;
import greencity.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventLikeNotificationTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserRepo userRepo;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private NotificationFormatter notificationFormatter;

    @InjectMocks
    private EventLikeListener eventLikeListener;

    @Test
    void shouldCreateNotificationWhenEventIsLiked() {
        User liker = new User();
        liker.setName("TestLiker");
        User eventCreator = new User();
        eventCreator.setId(2L);
        Event likedEvent = new Event();
        likedEvent.setId(10L);
        likedEvent.setTitle("Test Event");
        likedEvent.setCreator(eventCreator);

        ZonedDateTime now = ZonedDateTime.now();
        EventLikeEvent event = new EventLikeEvent(this, likedEvent, liker, now);

        String formattedDate = "Today 10:00 a.m";
        String expectedText = "TestLiker likes your event Test Event. Today 10:00 a.m";

        UserVO eventCreatorVO = new UserVO();
        eventCreatorVO.setId(2L);

        when(notificationFormatter.formatDateAndTime(any(ZonedDateTime.class)))
                .thenReturn(formattedDate);
        when(modelMapper.map(any(User.class), eq(UserVO.class)))
                .thenReturn(eventCreatorVO);

        eventLikeListener.handleEventLike(event);

        ArgumentCaptor<NewLikeNotificationDto> notificationDtoCaptor = ArgumentCaptor.forClass(NewLikeNotificationDto.class);

        verify(notificationService, times(1)).createNotification(notificationDtoCaptor.capture());

        NewLikeNotificationDto capturedDto = notificationDtoCaptor.getValue();

        assertEquals(false, capturedDto.isRead());
        assertEquals(eventCreatorVO.getId(), capturedDto.receiver().getId());
        assertEquals(expectedText, capturedDto.text());
        assertEquals(10L, capturedDto.eventId());
    }
}
