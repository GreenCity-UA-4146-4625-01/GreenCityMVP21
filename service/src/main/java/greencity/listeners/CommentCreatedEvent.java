package greencity.listeners;

import greencity.entity.EventComment;
import org.springframework.context.ApplicationEvent;

public class CommentCreatedEvent extends ApplicationEvent {
    private final EventComment eventComment;

    public CommentCreatedEvent(Object source, EventComment eventComment) {
        super(source);
        this.eventComment = eventComment;
    }

    public EventComment getEventComment() {
        return eventComment;
    }
}
