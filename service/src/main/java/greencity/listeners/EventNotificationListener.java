package greencity.listeners;

import greencity.dto.econewscomment.EcoNewsCommentDto;
import greencity.dto.notification.NewCommentNotificationDto;
import greencity.dto.user.UserVO;
import greencity.entity.EventComment;
import greencity.entity.User;
import greencity.exception.exceptions.NotFoundException;
import greencity.formatter.NotificationFormatter;
import greencity.repository.UserRepo;
import greencity.service.NotificationService;
import org.modelmapper.ModelMapper;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class EventNotificationListener {
    private final NotificationService notificationService;
    private final UserRepo userRepo;
    private final ModelMapper mapper;
    private final NotificationFormatter notificationFormatter;

    public EventNotificationListener(NotificationService notificationService, UserRepo userRepo, ModelMapper ModelMapper,NotificationFormatter notificationFormatter) {
        this.notificationService = notificationService;
        this.userRepo = userRepo;
        this.mapper = ModelMapper;
        this.notificationFormatter = notificationFormatter;
    }

    @EventListener
    public void handleEventCommentCreated(CommentCreatedEvent event) {
        EventComment comment = event.getEventComment();

        User eventAuthor = userRepo.findById(comment.getEvent().getCreator().getId())
                .orElseThrow(() -> new NotFoundException("Event author not found"));

        LocalDateTime createdDate = comment.getCreatedDate();
        ZonedDateTime zonedDateTime = createdDate.atZone(ZoneId.systemDefault());
        String fDateTime = notificationFormatter.formatDateAndTime(zonedDateTime);
        String finaltext = String.format("%s commented on your event %s. %s",
                event.getEventComment().getUser().getName(),event.getEventComment().getEvent().getTitle(),fDateTime);

        NewCommentNotificationDto notificationDto = NewCommentNotificationDto.builder()
                .isRead(false)
                .receiver(mapper.map(eventAuthor, UserVO.class))
                .comment(mapper.map(comment, EcoNewsCommentDto.class))
                .text(finaltext)
                .build();

        notificationService.createNotification(notificationDto);
    }
}
