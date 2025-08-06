package greencity.listeners;

import greencity.dto.econewscomment.EcoNewsCommentDto;
import greencity.dto.notification.NewCommentNotificationDto;
import greencity.dto.notification.NotificationDto;
import greencity.dto.user.UserVO;
import greencity.entity.Event;
import greencity.entity.EventComment;
import greencity.entity.User;
import greencity.formatter.NotificationFormatter;
import greencity.repository.UserRepo;
import greencity.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventCommentNotificationTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserRepo userRepo;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private EventNotificationListener listener;

    @Mock
    private NotificationFormatter notificationFormatter;

    private CommentCreatedEvent event;
    private EventComment comment;
    private User eventAuthor;
    private User commentator;
    private Event mockEvent;
    private UserVO eventAuthorVO;
    private EcoNewsCommentDto commentDto;

    @BeforeEach
    void setUp() {
        eventAuthor = new User();
        eventAuthor.setId(1L);
        eventAuthor.setName("EventAuthorName");

        commentator = new User();
        commentator.setName("CommentatorName");

        mockEvent = new Event();
        mockEvent.setId(100L);
        mockEvent.setTitle("Eco Day");
        mockEvent.setCreator(eventAuthor);

        comment = new EventComment();
        comment.setCreatedDate(LocalDateTime.now());
        comment.setEvent(mockEvent);
        comment.setUser(commentator);

        event = new CommentCreatedEvent(this, comment);

        eventAuthorVO = new UserVO();
        eventAuthorVO.setId(1L);
        commentDto = new EcoNewsCommentDto();
        commentDto.setId(50L);

        when(userRepo.findById(eventAuthor.getId())).thenReturn(Optional.of(eventAuthor));
        when(mapper.map(eventAuthor, UserVO.class)).thenReturn(eventAuthorVO);
        when(mapper.map(comment, EcoNewsCommentDto.class)).thenReturn(commentDto);
    }

    @Test
    void shouldCreateNotificationWithCorrectData() {
        String formattedDateTime = "Today 10:00 a.m";
        String expectedNotificationText = "CommentatorName commented on your event Eco Day. Today 10:00 a.m";

        when(notificationFormatter.formatDateAndTime(any(ZonedDateTime.class)))
                .thenReturn(formattedDateTime);

        listener.handleEventCommentCreated(event);

        ArgumentCaptor<NotificationDto> dtoCaptor = ArgumentCaptor.forClass(NotificationDto.class);
        verify(notificationService, times(1)).createNotification(dtoCaptor.capture());

        NewCommentNotificationDto capturedDto = (NewCommentNotificationDto) dtoCaptor.getValue();

        assertEquals(false, capturedDto.isRead());
        assertEquals(eventAuthorVO, capturedDto.receiver());
        assertEquals(commentDto, capturedDto.comment());
        assertEquals(expectedNotificationText, capturedDto.text());
        assertEquals(commentDto.getId(), capturedDto.objectId());
    }
}