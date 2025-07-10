package greencity.mapping;

import greencity.dto.event.EventResponseDto;
import greencity.entity.Event;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class EventResponseDtoMapper extends AbstractConverter<Event, EventResponseDto> {
    @Override
    protected EventResponseDto convert(Event event) {
        if (event == null) return null;

        EventResponseDto dto = new EventResponseDto();
        dto.setEventId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setCreatedAt(event.getCreationDate());

        return dto;
    }
}