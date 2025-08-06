package greencity.listeners;

import greencity.entity.Event;
import greencity.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.ZonedDateTime;

@Getter
public class EventLikeEvent extends ApplicationEvent {
    private final Event event;
    private final User liker;
    private final ZonedDateTime timecreation;

    public EventLikeEvent(Object source, Event event, User liker, ZonedDateTime now) {
        super(source);
        this.event = event;
        this.liker = liker;
        this.timecreation=ZonedDateTime.now();
    }
}
