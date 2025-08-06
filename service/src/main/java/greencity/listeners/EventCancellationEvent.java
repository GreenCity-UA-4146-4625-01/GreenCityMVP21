package greencity.listeners;

import greencity.entity.User;

import java.util.Set;

public class EventCancellationEvent {
    private final Long eventId;
    private final String eventName;
    private final Set<User> participants;

    public EventCancellationEvent(Object source, Long eventId, String eventName, Set<User> participants) {
        super();
        this.eventId = eventId;
        this.eventName = eventName;
        this.participants = participants;
    }

    public Long getEventId() {
        return eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public Set<User> getParticipants() {
        return participants;
    }
}
