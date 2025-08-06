package greencity.listeners;

import greencity.dto.notification.NewCancelationNotificationDto;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.formatter.NotificationFormatter;
import greencity.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class EventCancellationNotificationTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private NotificationFormatter notificationFormatter;

    @InjectMocks
    private EventCancelListener listener;

    private EventCancellationEvent event;
    private Set<User> participants;

    @BeforeEach
    void setUp() {
        User user1 = User.builder().id(1L).name("User1").build();
        User user2 = User.builder().id(2L).name("User2").build();
        participants = new HashSet<>();
        participants.add(user1);
        participants.add(user2);

        Long eventId = 10L;
        String eventName = "Test Event";
        event = new EventCancellationEvent(this, eventId, eventName, participants);
    }

    @Test
    void shouldCreateNotificationForEachParticipantWithCorrectData() {
        // Arrange
        Long eventId = 10L;
        String eventName = "Test Event";
        String formattedDateTime = "Today 10:00 a.m";
        String expectedNotificationText = "Unfortunately, event 'Test Event' was cancelled. Today 10:00 a.m";

        UserVO user1VO = UserVO.builder().id(1L).build();
        UserVO user2VO = UserVO.builder().id(2L).build();

        when(modelMapper.map(any(User.class), eq(UserVO.class)))
                .thenReturn(user1VO)
                .thenReturn(user2VO);

        when(notificationFormatter.formatDateAndTime(any(ZonedDateTime.class)))
                .thenReturn(formattedDateTime);

        // Act
        listener.handleEventCancellation(event);

        // Assert
        ArgumentCaptor<NewCancelationNotificationDto> dtoCaptor = ArgumentCaptor.forClass(NewCancelationNotificationDto.class);
        verify(notificationService, times(participants.size()))
                .createNotification(dtoCaptor.capture());

        List<NewCancelationNotificationDto> capturedDtos = dtoCaptor.getAllValues();
        assertEquals(participants.size(), capturedDtos.size());

        // We check if the DTOs were created for the correct users and with the correct data
        Set<Long> capturedReceiverIds = capturedDtos.stream()
                .map(dto -> dto.receiver().getId())
                .collect(Collectors.toSet());

        // Check if all participants received a notification
        assertEquals(2, capturedReceiverIds.size());

        // Check the content of each DTO
        capturedDtos.forEach(dto -> {
            assertEquals(false, dto.isRead());
            assertEquals(eventId, dto.eventId());
            assertEquals(eventName, dto.eventName());
            assertEquals(expectedNotificationText, dto.text());
        });
    }
}