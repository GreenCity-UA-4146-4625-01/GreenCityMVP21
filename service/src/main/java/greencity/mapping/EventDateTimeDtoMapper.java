package greencity.mapping;

import greencity.dto.event.EventDateTimeDto;
import greencity.entity.EventDateTime;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class EventDateTimeDtoMapper extends AbstractConverter<EventDateTimeDto, EventDateTime> {

    @Override
    protected EventDateTime convert(EventDateTimeDto dto) {
        if(dto==null) return null;

        return EventDateTime.builder()
                .date(dto.getDate())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .allDay(dto.isAllDay())
                .build();
    }
}
