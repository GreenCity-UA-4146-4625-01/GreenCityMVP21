package greencity.listeners;

import greencity.dto.notification.NewCancelationNotificationDto;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.formatter.NotificationFormatter;
import greencity.service.EventService;
import greencity.service.NotificationService;
import org.modelmapper.ModelMapper;
import org.springframework.context.event.EventListener;

import java.time.ZonedDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public class EventCancelListener {
    private final NotificationService notificationService;
    private final EventService eventService;
    private final ModelMapper modelMapper;
    private final NotificationFormatter notificationFormatter;

    public EventCancelListener(NotificationService notificationService,EventService eventService,ModelMapper modelMapper, NotificationFormatter notificationFormatter){
        this.eventService = eventService;
        this.notificationService = notificationService;
        this.modelMapper = modelMapper;
        this.notificationFormatter = notificationFormatter;
    }
    @EventListener
    public void handleEventCancellation(EventCancellationEvent event){
        Long canceledEventId = event.getEventId();
        String eventName = event.getEventName();
        Set<User> participants = event.getParticipants();
        ZonedDateTime now = ZonedDateTime.now();
        String fDateTime = notificationFormatter.formatDateAndTime(now);

        String finalNotificationText = String.format("Unfortunately, event '%s' was cancelled. %s",
                eventName, fDateTime);

        Set<NewCancelationNotificationDto> notifications = participants.stream()
                .map(participant -> NewCancelationNotificationDto.builder()
                        .isRead(false)
                        .receiver(modelMapper.map(participant, UserVO.class))
                        .eventId(canceledEventId)
                        .eventName(eventName)
                        .text(finalNotificationText)
                        .build())
                .collect(Collectors.toSet());

        notifications.forEach(notificationService::createNotification);
    }
}
