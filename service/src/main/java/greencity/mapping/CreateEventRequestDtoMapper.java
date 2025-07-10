package greencity.mapping;
import greencity.dto.event.CreateEventRequestDto;
import greencity.dto.event.EventDateTimeDto;
import greencity.dto.event.EventImageDto;
import greencity.dto.event.EventLocationDto;
import greencity.entity.Event;
import greencity.entity.EventDateTime;
import greencity.entity.EventImage;
import greencity.entity.EventLocation;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CreateEventRequestDtoMapper extends AbstractConverter<CreateEventRequestDto, Event> {
    @Override
    protected Event convert(CreateEventRequestDto dto) {
        if (dto == null) return null;

        Event event = Event.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .eventVisibility(dto.getVisibility())
                .eventTypes(dto.getEventTypes())
                .mainImageId(dto.getMainImageId())
                .build();

        if (dto.getLocations() != null) {
            List<EventLocation> locations = new ArrayList<>();
            for (EventLocationDto locationDto : dto.getLocations()) {
                EventLocation location = EventLocation.builder()
                        .address(locationDto.getAddress())
                        .latitude(locationDto.getLatitude())
                        .longitude(locationDto.getLongitude())
                        .event(event)
                        .build();
                locations.add(location);
            }
            event.setEventLocations(locations);
        }

        if (dto.getEventDateTimes() != null) {
            List<EventDateTime> times = new ArrayList<>();
            for (EventDateTimeDto timeDto : dto.getEventDateTimes()) {
                EventDateTime time = EventDateTime.builder()
                        .date(timeDto.getDate())
                        .startTime(timeDto.getStartTime())
                        .endTime(timeDto.getEndTime())
                        .allDay(timeDto.isAllDay())
                        .event(event)
                        .build();
                times.add(time);
            }
            event.setEventDateTimes(times);
        }

        if (dto.getImages() != null) {
            List<EventImage> images = new ArrayList<>();
            for (EventImageDto imageDto : dto.getImages()) {
                EventImage image = EventImage.builder()
                        .id(imageDto.getImageId())
                        .isMain(imageDto.getIsMain())
                        .event(event)
                        .build();
                images.add(image);
            }
            event.setEventImages(images);
        }

        return event;
    }
}