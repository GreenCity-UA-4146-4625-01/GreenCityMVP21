package greencity.mapping;

import greencity.dto.event.EventDateTimeDto;
import greencity.dto.event.EventImageDto;
import greencity.dto.event.EventLocationDto;
import greencity.dto.event.EventResponseDto;
import greencity.entity.Event;
import greencity.entity.EventDateTime;
import greencity.entity.EventImage;
import greencity.entity.EventLocation;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EventResponseDtoMapper extends AbstractConverter<Event, EventResponseDto> {

    @Override
    protected EventResponseDto convert(Event event) {
        return EventResponseDto.builder()
                .eventId(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .visibility(event.getEventVisibility())
                .eventTypes(event.getEventTypes())
                .eventDateTimes(mapEventDateTimes(event.getEventDateTimes()))
                .locations(mapEventLocations(event.getEventLocations()))
                .onlineLinks(event.getOnlineLinks())
                .images(mapImages(event.getEventImages()))
                .mainImageId(event.getMainImageId())
                .createdAt(event.getCreationDate())
                .build();
    }
    private List<EventDateTimeDto> mapEventDateTimes(List<EventDateTime> times) {
        if (times == null) {
            return null;
        }
        List<EventDateTimeDto> result = new ArrayList<>();
        for (EventDateTime t : times) {
            result.add(new EventDateTimeDto(
                    t.getDate(),
                    t.getStartTime(),
                    t.getEndTime(),
                    t.isAllDay()
            ));
        }
        return result;
    }

    private List<EventLocationDto> mapEventLocations(List<EventLocation> locations) {
        if (locations == null) {
            return null;
        }
        List<EventLocationDto> result = new ArrayList<>();
        for (EventLocation l : locations) {
            result.add(new EventLocationDto(
                    l.getAddress(),
                    l.getLatitude(),
                    l.getLongitude()
            ));
        }
        return result;
    }

    private List<EventImageDto> mapImages(List<EventImage> images) {
        if (images == null) {
            return null;
        }
        List<EventImageDto> result = new ArrayList<>();
        for (EventImage i : images) {
            result.add(new EventImageDto(
                    i.getId(),
                    i.getUrl(),
                    i.getIsMain()
            ));
        }
        return result;
    }
}