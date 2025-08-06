package greencity.listeners;

import greencity.dto.notification.NewLikeNotificationDto;
import greencity.dto.user.UserVO;
import greencity.entity.Event;
import greencity.entity.User;
import greencity.formatter.NotificationFormatter;
import greencity.service.NotificationService;
import org.modelmapper.ModelMapper;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
public class EventLikeListener {
    private final NotificationService notificationService;
    private final ModelMapper modelMapper;
    private final NotificationFormatter notificationFormatter;

    public EventLikeListener(NotificationService notificationService,ModelMapper modelMapper, NotificationFormatter notificationFormatter){
        this.notificationService=notificationService;
        this.modelMapper = modelMapper;
        this.notificationFormatter=notificationFormatter;
    }
    @EventListener
    public void handleEventLike(EventLikeEvent event){
        Event likedEvent=event.getEvent();
        User userl=event.getLiker();
        ZonedDateTime createdDate = event.getTimecreation();
        String fDateTime = notificationFormatter.formatDateAndTime(createdDate);
        String finaltext = String.format("%s likes your event %s. %s",
                userl.getName(),likedEvent.getTitle(),fDateTime
                );

        NewLikeNotificationDto notificationDto = NewLikeNotificationDto.builder()
                .isRead(false)
                .receiver(modelMapper.map(likedEvent.getCreator(), UserVO.class))
                .eventId(event.getEvent().getId())
                .text(finaltext)
                .build();

        notificationService.createNotification(notificationDto);
    }
}
